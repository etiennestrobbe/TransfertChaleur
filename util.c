
#include "util.h"

static int gid = 0;
/* Constructeur et destructeur */
Fifo fifo_allocate()
{
  Fifo q;

  q = malloc(sizeof(struct def_fifo));
  q->mutex = malloc(sizeof(pthread_mutex_t));
  pthread_mutex_init(q->mutex, NULL);
  q->condition = malloc(sizeof(pthread_cond_t));
  pthread_cond_init(q->condition, NULL);
  q->value = 20.0;
  q->state = FULL;
  q->id = gid++;

  return q;
}

void fifo_free(Fifo q)
{
	free(q->mutex);
	free(q->condition);
	free(q);
}

/* Ajouter un element */
void fifo_put(Fifo q, const double t)
{
	pthread_mutex_lock(q->mutex);
	while(q->state == FULL){
		pthread_cond_wait(q->condition,q->mutex);
	}
	q->value = t;
	q->state = FULL;
	
	pthread_mutex_unlock(q->mutex);
	pthread_cond_signal(q->condition);
}

/* Retirer un element */
double fifo_get(const Fifo q)
{
	pthread_mutex_lock(q->mutex);
	while(q->state == EMPTY){
		pthread_cond_wait(q->condition,q->mutex);
	}
	q->state = EMPTY;
	
	pthread_mutex_unlock(q->mutex);
	pthread_cond_signal(q->condition);
	return q->value;
}

/* Impression de la fifo */
void fifo_print(const Fifo q)
{
	printf("{Fifo [value=%f]} \n", q->value);
}

void param_free(Param_Fifo param){
	free(param->lire_droite);
	free(param->ecrire_droite);
	free(param->lire_gauche);
	free(param->ecrire_gauche);
	free(param);
}
