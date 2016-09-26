#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <assert.h>


typedef struct _Sect {
    char     name[8]; // Not NULL-terminated!
    uint32_t offs;    // From the beginning of the file.
    uint32_t size;    // In bytes.
} Sect;

// Each section has one at the beginning.
typedef struct _SectHeader {
    char     name[8]; // Not NULL-terminated!
    uint32_t size;
} SectHeader;

typedef struct _EDXFileHeader {
    uint32_t magic;      // Must be 0x49584445
    uint32_t version;    // Usually 0x10000000 or 0x00000001
    uint64_t magic2;     // Must be 0x2020207366666F2E
    uint32_t section_table_size;
    Sect*    section_table;
} EDXFileHeader;


int main(int argc, const char* argv[]) {
    uint32_t i;
    FILE*    edx_stream;

    edx_stream = fopen(argv[1], "rb");
    assert(edx_stream);

    EDXFileHeader edx;
    fread(&edx.magic,   sizeof(edx.magic),   1, edx_stream);
    fread(&edx.version, sizeof(edx.version), 1, edx_stream);
    fread(&edx.magic2,  sizeof(edx.magic2),  1, edx_stream);

    fread(&edx.section_table_size,
          sizeof(edx.section_table_size),
          1, 
          edx_stream);
    edx.section_table = (Sect*) malloc(edx.section_table_size);
    fread(edx.section_table, edx.section_table_size, 1, edx_stream);

    printf("magic   = 0x%8x\n",    edx.magic);
    printf("version = 0x%8x\n",    edx.version);
    printf("magic2  = 0x%8x%8x\n", edx.magic2);

    for (i = 0;
         i < (edx.section_table_size / sizeof(Sect));
         i += 1) {
        Sect* section = edx.section_table[i];

        printf("%d\n", i);
        printf(" name = %.*s\n", sizeof(section.name), section.name);
        printf(" offs = 0x%x\n", section.offs);
        printf(" size = 0x%x\n", section.size);

        fseek(edx_stream, section, SEEK_SET);

        char* raw = malloc(section.size);
        fread(raw, section.size, 1, edx_stream);
        //...
        free(raw);
    }

    fclose(edx_stream);
}