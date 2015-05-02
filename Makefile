CC=gcc
CFLAGS=-Wall -std=c99 -pthread
LDFLAGS=-lpthread

EXE= main

all: $(EXE)

$(EXE): util.o util.h main.c
	$(CC) $(CFLAGS) -o $(EXE) main.c util.o

clean:
	/bin/rm -f $(EXE) *~ *.o
