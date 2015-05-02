#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
/* Definition de la FIFO */
struct def_fifo{
	pthread_cond_t *condition;
	pthread_mutex_t *mutex;
	enum {EMPTY,FULL} state;
	double value;
	int id;
};

typedef struct def_fifo *Fifo;

/* Definition d'une structure pour le parametre des threads */
struct def_param{
	Fifo lire_gauche;
	Fifo lire_droite;
	Fifo ecrire_droite;
	Fifo ecrire_gauche;
	int iteration;
	int id;
};

typedef struct def_param *Param_Fifo;

/* Constructeur et destructeur */
Fifo fifo_allocate();

void fifo_free(Fifo q);

/* Ajouter un element */
void fifo_put(Fifo q, const double t);

/* Retirer un element */
double fifo_get(const Fifo q);

/* Impression de la fifo */
void fifo_print(const Fifo q);

/* Destructeur */
void param_free(Param_Fifo param);
