#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>

struct def_fifo{
	enum {EMPTY,FULL} state;
	double value;
};

typedef struct def_fifo *Fifo;

/* Constructeur et destructeur */
Fifo fifo_allocate()
{
  Fifo q;

  q = malloc(sizeof(struct def_fifo));
  q->value = 0.0;
  q->state = EMPTY;

  return q;
}

void fifo_free(Fifo q)
{
  free(q);
}

/* Ajouter un element */
void fifo_put(Fifo q, const double t)
{
  if (q->state == FULL)
    fprintf(stderr, "Fifo is full (not added)\n");
  else{
		q->value = t;
		q->state = FULL;
	}
}

/* Retirer un element */
double fifo_get(const Fifo q)
{
  if (q->state == EMPTY) {
    fprintf(stderr, "Fifo is empty\n");
    return 42; /* ou toute autre valeur */
  }
  q->state = EMPTY;
  return q->value;
}

/* Impression de la fifo */
void fifo_print(const Fifo q)
{

	if(q->state == EMPTY)
		printf("{Fifo [state=EMPTY, value=%f]} \n", q->value);
	else
		printf("{Fifo [state=FULL, value=%f]} \n", q->value);
}


int main(){
	Fifo banane;
	banane = fifo_allocate(banane);
	fifo_print(banane);
	fifo_put(banane,3.5);
	fifo_print(banane);
	printf("GET = %f\n",fifo_get(banane));
	fifo_print(banane);
	fifo_get(banane);
	fifo_free(banane);
	return 0;
}
