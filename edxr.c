#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <assert.h>

// Note:
// some legacy *.waw files may be found in place of *.edx files
// EDX files may have following sections: PCM, WAZ, WAW, precached lists

typedef struct _Sect {
    char     name[8]; // Not NULL-terminated
    uint32_t offs;
    uint32_t size;    // In bytes.
} Sect;

// Each section has one at the beginning
typedef struct _SectHDR {
    char     name[8]; // Not NULL-terminated
    uint32_t size;
} SectHDR;

typedef struct _EDXFile {
    uint32_t magic;      // Must be 0x49584445
    uint32_t version;    // 1
    char     junk[8];
    uint32_t sects_size;
    Sect*    sects;
} EDXFile;


int main(int argc, const char* argv[]) {
    uint32_t i;

    FILE* edx_stream = fopen(/* argv[1] */ "test.edx", "rb");
    assert(edx_stream);

    EDXFile edx;
    fread(&edx.magic,      sizeof(edx.magic),      1, edx_stream);
    fread(&edx.version,    sizeof(edx.version),    1, edx_stream);
    fread(&edx.junk,       sizeof(edx.junk),       1, edx_stream);
    fread(&edx.sects_size, sizeof(edx.sects_size), 1, edx_stream);
    edx.sects = (Sect*) malloc(edx.sects_size);
    fread(edx.sects,       edx.sects_size,         1, edx_stream);

    printf("magic   = 0x%x\n", edx.magic);
    printf("version = 0x%x\n", edx.version);

    for (i = 0; i < (edx.sects_size / sizeof(Sect)); i += 1) {
        printf("%d.\n", i);
        printf(" name = %.*s\n",
            sizeof(edx.sects[i].name), edx.sects[i].name);
        printf(" offs = 0x%x\n", edx.sects[i].offs);
        printf(" size = 0x%x (0x%x)\n",
            edx.sects[i].size, sizeof(SectHDR) + edx.sects[i].size);
    }

    fclose(edx_stream);
}   