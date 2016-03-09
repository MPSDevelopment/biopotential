/* WAVE RIFF (PCM) to WAW converter program */
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <locale.h>
#include <assert.h>
#include <math.h>


typedef struct _WAVE_file
{
	char     chunk_id[4];
	uint32_t chunk_size;
	char     format[4];
	char     subchunk1_id[4];
	uint32_t subchunk1_size;
	uint16_t audio_format;
	uint16_t num_channels;
	uint32_t sample_rate;
	uint32_t byte_rate;
	uint16_t block_align;
	uint16_t bits_per_sample;
	char     subchunk2_id[4];
	uint32_t subchunk2_size;
	uint8_t* data;
}
WAVE_file;

typedef struct _Chunk
{
	int32_t count;
	float*  data;
	float   avg;
	float   diff;
}
Chunk;

typedef struct _WAW_file
{
	uint16_t num_chunks;
	Chunk*   chunks;
}
WAW_file;


static void wave_init(FILE* input, WAVE_file* wave)
{
	fread(wave->chunk_id,         4, 1, input);
	fread(&wave->chunk_size,      4, 1, input);
	fread(wave->format,           4, 1, input);
	fread(wave->subchunk1_id,     4, 1, input);
	fread(&wave->subchunk1_size,  4, 1, input);
	fread(&wave->audio_format,    2, 1, input);
	fread(&wave->num_channels,    2, 1, input);
	fread(&wave->sample_rate,     4, 1, input);
	fread(&wave->byte_rate,       4, 1, input);
	fread(&wave->block_align,     2, 1, input);
	fread(&wave->bits_per_sample, 2, 1, input);
	/* skip junk data */
	fseek(input, wave->subchunk1_size - 16, SEEK_CUR);
	fread(wave->subchunk2_id,     4, 1, input);
	fread(&wave->subchunk2_size,  4, 1, input);

	assert(memcmp(wave->chunk_id,     "RIFF", 4) == 0);
	assert(memcmp(wave->format,       "WAVE", 4) == 0);
	assert(memcmp(wave->subchunk1_id, "fmt ", 4) == 0);
	assert(memcmp(wave->subchunk2_id, "data", 4) == 0);
	/* we only take PCM! */
	assert(wave->audio_format == 1);

	wave->data = (uint8_t*)malloc(wave->subchunk2_size);
	/* reading all of the bytes it has */
	fread(wave->data, wave->subchunk2_size, 1, input);
}

static void waw_to_file(FILE* output, const WAW_file* waw)
{
	fwrite(&waw->num_chunks, 2, 1, output);

	for (uint16_t i = 0; i < waw->num_chunks; ++i)
	{
		Chunk* v = &waw->chunks[i];

		fwrite(&v->count, 4, 1, output);
		/* writing sequential array of IEEE 754 32 bit floats */
		fwrite(v->data, 4 * v->count, 1, output);
	}
}

/*
static double chunk_compute_1(const Chunk* data)
{
	double result = 0.0;

	for (int32_t i = 0; i < data->count; ++i)
		result += data->data[i];

	return result;
}

static double chunk_compute_2(const Chunk* data, double avg)
{
	double result = 0.0;

	for (int32_t i = 0; i < data->count; ++i)
	{
		double diff = data->data[i] - avg;
		result += diff * diff;
	}

	return result;
}

static void chunk_compute(Chunk* data)
{
	data->avg  = chunk_compute_1(data) / (double)data->count;
	data->diff = sqrt(chunk_compute_2(data, data->avg) /
	                  (double)data->count);
}
*/

static void wave_to_waw(const WAVE_file* wave, WAW_file* waw)
{
	memset(waw, 0, sizeof(WAW_file));

	float* pcm = (float*)malloc(sizeof(float) * wave->subchunk2_size);

	for (uint32_t i = 0; i < wave->subchunk2_size; ++i)
		pcm[i] = (double)(char)(wave->data[i] ^ 0x80) / 128.0;

	int32_t count = (wave->subchunk2_size - 5) / 2;

	float *tbuf = (float*)malloc(sizeof(float) * count);

	for (int32_t i = 0; count >= 1; ++i)
	{
		waw->num_chunks = i+1;
		waw->chunks = realloc(waw->chunks, sizeof(Chunk) * waw->num_chunks);

		Chunk* pcm_chunk = &waw->chunks[i];

		pcm_chunk->count = count;
		pcm_chunk->data  = (float*)malloc(sizeof(float) * pcm_chunk->count);

		for (int32_t j = 0; j < count; ++j)
		{
			/* works gut */

			pcm_chunk->data[j] = pcm[2 + j*2 - 2] * 0.0352262918821   +
			                     pcm[2 + j*2 - 1] * 0.08544127388224  +
			                     pcm[2 + j*2]     * -0.13501102001039 +
			                     pcm[2 + j*2 + 1] * -0.45987750211933 +
			                     pcm[2 + j*2 + 2] * 0.80689150931334  +
			                     pcm[2 + j*2 + 3] * -0.33267055295096;

			tbuf[j] = pcm[2 + j*2 - 2] * 0.33267055295096  +
			          pcm[2 + j*2 - 1] * 0.80689150931334  +
			          pcm[2 + j*2]     * 0.45987750211933  +
			          pcm[2 + j*2 + 1] * -0.13501102001039 +
			          pcm[2 + j*2 + 2] * -0.08544127388224 +
			          pcm[2 + j*2 + 3] * 0.0352262918821;
		}

		/* chunk_compute(pcm_chunk); */

		/* prevent leaks */
		if (pcm != tbuf)
			free(pcm);

		pcm = tbuf;
		count = (count - 5) / 2;
	}

	free(tbuf);
}

int main(int argc, const char* argv[])
{
	assert(sizeof(Chunk) == 16);

	setlocale(LC_ALL, "rus");

	FILE* wave_stream = fopen(argv[1], "rb");
	assert(wave_stream);

	WAVE_file wave;
	wave_init(wave_stream, &wave);

	fclose(wave_stream);


	WAW_file waw;
	wave_to_waw(&wave, &waw);


	FILE* waw_stream = fopen(argv[2], "wb");
	assert(waw_stream);

	waw_to_file(waw_stream, &waw);

	return 0;
}