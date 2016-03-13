/* WAW comparator program */
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <locale.h>
#include <ctype.h>
#include <math.h>
#include <assert.h>


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

typedef struct _WAW_result
{
	float   avg;
	float   diff;
	int32_t lvl;
}
WAW_result;


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
	/* this is done to prevent precision losses */
	double avg = chunk_compute_1(data) / (double)data->count;

	data->avg  = avg;
	data->diff = sqrt(chunk_compute_2(data, avg) / (double)data->count);
}

static void waw_init(FILE* input, WAW_file* waw)
{
	fread(&waw->num_chunks, 2, 1, input);
	waw->chunks = (Chunk*)malloc(sizeof(Chunk) * waw->num_chunks);

	for (uint16_t i = 0; i < waw->num_chunks; ++i)
	{
		int32_t count = 0;
		fread(&count, 4, 1, input);

		/* reading sequential array of IEEE 754 32 bit floats */
		float* data = (float*)malloc(sizeof(float) * count);
		fread(data, sizeof(float) * count, 1, input);

		waw->chunks[i].count = count;
		waw->chunks[i].data = data;

		chunk_compute(&waw->chunks[i]);
	}
}

static void waw_compare(const WAW_file* waw,
                        const WAW_file* to,
                        WAW_result* result)
{
	memset(result, 0, sizeof(WAW_result));

	double avg  = 0.0;
	double diff = 0.0;

	for (uint16_t i = 0; i < waw->num_chunks; ++i)
	{
		avg  += fabs(waw->chunks[i].avg  - to->chunks[i].avg);
		diff += fabs(waw->chunks[i].diff - to->chunks[i].diff);
	}

	result->avg  = avg  / (double)waw->num_chunks;
	result->diff = diff / (double)waw->num_chunks;

	double diff_64 = (double)result->diff;
	result->lvl = *(int32_t*)&diff_64;
}

int32_t main(int32_t argc, const char* argv[])
{
	assert(sizeof(Chunk) == 16);

	setlocale(LC_ALL, "rus");

	FILE* waw_1_stream = fopen(argv[1], "rb");
	FILE* waw_2_stream = fopen(argv[2], "rb");

	assert(waw_1_stream);
	assert(waw_2_stream);

	WAW_file waw_1;
	waw_init(waw_1_stream, &waw_1);

	WAW_file waw_2;
	waw_init(waw_2_stream, &waw_2);

	fclose(waw_1_stream);
	fclose(waw_2_stream);

	assert(waw_1.num_chunks == waw_2.num_chunks);

	WAW_result result;
	waw_compare(&waw_1, &waw_2, &result);

	printf("%f %f %d\n", result.avg, result.diff, result.lvl);
}