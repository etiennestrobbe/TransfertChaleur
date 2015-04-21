CC=gcc
CFLAGS=-Wall  -std=c99 -pthread
SRC = $(wildcard *.c)

EXE = $(SRC:.c=)

all: $(EXE) -lpthread

clean:
	rm -f $(EXE)

