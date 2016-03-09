/* NBS db reader program, use Cyrillic/Windows 1521 encoding */
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <locale.h>
#include <assert.h>


typedef struct _NBS_tape
{
	int32_t tape_num;
	int32_t row;
	int32_t column;
}
NBS_tape;

typedef struct _NBS_entry
{
	char name[64];
	char real_name[64];
	char note[256];
	NBS_tape tape;
}
NBS_entry;

typedef struct _NBS_db
{
	char name[64];
	int32_t num_entries;
	NBS_entry* entries;
}
NBS_db;

typedef struct _NBS_file
{
	char name[64];
	int32_t num_dbs;
	NBS_db* dbs;
}
NBS_file;


static void get_tape_file(const NBS_tape* tape, char* tape_file)
{
	sprintf(tape_file, "Кассета_%d\\К%02d%02d%02d.waw",
	        tape->tape_num, tape->tape_num, tape->row, tape->column);
}

static void read_until(FILE* fh,
                       char* dest,
                       char  term)
{
	int32_t i = 0;

	while (1)
	{
		char c = fgetc(fh);
		if (feof(fh) || c == term)
			break;

		dest[i] = c;
		i++;
	}
}

static void nbs_init(FILE* input, NBS_file* nbs)
{
	/* skip first 3 bytes (magic) */
	fseek(input, 3, SEEK_SET);
	/* read 1 byte, which tells us where db start marker is at */
	/* + calculate name length and read it */
	int32_t nbs_name_length = fgetc(input) - 4;
	fread(nbs->name, nbs_name_length, 1, input);
}

static void nbs_read_entries(FILE* input, NBS_db* db)
{
	read_until(input, db->name, '\x0a');
	fseek(input, 6, SEEK_CUR);

	db->num_entries = 0;
	db->entries = NULL;

	while (1)
	{
		NBS_entry entry;
		memset(&entry, 0, sizeof(NBS_entry));

		read_until(input, entry.name, '#');

		if (feof(input))
			break;

		read_until(input, entry.note, '#');

		char raw_tape[64] = {0};
		read_until(input, raw_tape, '\n');

		entry.tape.tape_num = atoi(strtok(raw_tape, "."));
		entry.tape.row      = atoi(strtok(NULL, "."));
		entry.tape.column   = atoi(strtok(NULL, "."));

		db->num_entries++;
		db->entries = realloc(db->entries,
		                      db->num_entries * sizeof(NBS_entry));
		db->entries[db->num_entries-1] = entry;
	}
}

static void nbs_read_dbs(FILE* input, NBS_file* nbs)
{
	/* skip another 4 bytes: magic and some weird bytes */
	fseek(input, 4, SEEK_CUR);

	nbs->num_dbs = 1; /* fgetc(input); */
	fseek(input, 1, SEEK_CUR); /* ~^ */
	nbs->dbs = malloc(sizeof(NBS_db) * nbs->num_dbs);
	memset(nbs->dbs, 0, sizeof(NBS_db));

	for (int32_t i = 0; i < nbs->num_dbs; ++i)
	{
		NBS_db db;
		nbs_read_entries(input, &db);

		nbs->dbs[i] = db;
	}
}

int32_t main(int32_t argc, const char* argv[])
{
	setlocale(LC_ALL, "rus");

	FILE* nbs_stream = fopen(argv[1], "rb");
	assert(nbs_stream);

	NBS_file nbs;
	nbs_init(nbs_stream, &nbs);

	nbs_read_dbs(nbs_stream, &nbs);

	/* */
	char eng_cmd[256];
	sprintf(eng_cmd, "eng %s %s.waw", argv[2], argv[2]);
	system(eng_cmd);
	/* */

	printf("%s (%i) ->\n", nbs.name, nbs.num_dbs);

	for (int32_t i = 0; i < nbs.num_dbs; ++i)
	{
		const NBS_db* db = &nbs.dbs[i];
		printf(" %s (%i) ->\n", db->name, db->num_entries);

		for (int32_t j = 0; j < db->num_entries; ++j)
		{
			const NBS_entry* entry = &db->entries[j];

			printf("%3i %-18s %-20s ", j, entry->name, entry->note);

			/* */
			char tape_file[256];
			get_tape_file(&entry->tape, tape_file);

			char waw_cmd[256];
			sprintf(waw_cmd, "waw %s.waw %s", argv[2], tape_file);
			system(waw_cmd);
			/* */
		}
	}

	return 0;
}