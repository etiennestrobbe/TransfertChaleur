#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>


/* Definition de la FIFO */
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

/* Definition d'une structure pour le parametre des threads */


pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t not_empty = PTHREAD_COND_INITIALIZER;
pthread_cond_t not_full = PTHREAD_COND_INITIALIZER;

double DT = 600.0;
double DX = 0.04;
double constanteMur;
double constanteIso;

struct def_param{
	Fifo get_avant;
	Fifo get_apres;
	Fifo put_avant;
	Fifo put_apres;
};

typedef struct def_param *Param_Fifo;

Param_Fifo param_allocate(){
	
	Param_Fifo param = malloc(sizeof(struct def_param));
	param->get_avant = fifo_allocate();
	param->get_apres = fifo_allocate();
	param->put_avant = fifo_allocate();
	param->put_apres = fifo_allocate();
	
	return param;
}

void param_free(Param_Fifo param){
	free(param->get_avant);
	free(param->get_apres);
	free(param->put_apres);
	free(param->put_avant);
	free(param);
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

void *boucleCalcul(void *arg){

	//TODO mettre l'iteration max
	Param_Fifo *a = ((Param_Fifo *) arg);
	for(;;) {

		pthread_mutex_lock(&mutex);

		//pthread_cond_wait(&not_full, &mutex);

		//if(!fifo_is_empty(Q)) {
			printf("valeur du thread %f\n",fifo_get((*a)->get_avant));
			//fifo_put(Q,i);
			//pthread_cond_signal(&not_empty);
		//}
		pthread_mutex_unlock(&mutex);
		sleep(1);
	}
	return 0;
}

void init_constantes(){
	constanteMur = (0.84 * DT) / (1400 * 840 * DX * DX);
    constanteIso = (0.04 * DT) / (30 * 900 * DX * DX);
}


int main(){
	pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
	pthread_mutex_init(&mutex, NULL);
	init_constantes();
	int i;
	
	/* initialisation des threads et des différentes fifos */
	pthread_t *threads[7];
	Param_Fifo *list_param[7];
	Fifo put_avant,get_avant;
	for(i=0;i<7;i++){
		pthread_t th;
		threads[i] = &th;
		Param_Fifo param = param_allocate();
		if(i!=0){
			free(param->put_avant);
			free(param->get_avant);
			param->get_avant = get_avant;
			param->put_avant = put_avant;
		}
		pthread_create(threads[i], NULL, boucleCalcul, &param);
		put_avant = param->get_apres;
		get_avant = param->put_apres;
		list_param[i] =&param;				
	}
	
	// attente du pere
	for(i=0;i<7;i++){
		pthread_join(*threads[i], NULL);
	}
	
	for(i=0;i<7;i++){
		param_free(*list_param[i]);	
	}

	return 0;
}
