#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>


/* Definition de la FIFO */
struct def_fifo{
	pthread_cond_t *full;
	pthread_mutex_t *mutex;
	enum {EMPTY,FULL} state;
	double value;
};

typedef struct def_fifo *Fifo;

/* Constructeur et destructeur */
Fifo fifo_allocate()
{
  Fifo q;

  q = malloc(sizeof(struct def_fifo));
  q->mutex = malloc(sizeof(pthread_mutex_t));
  pthread_mutex_init(q->mutex, NULL);
  q->full = malloc(sizeof(pthread_cond_t));
  pthread_cond_init(q->full, NULL);
  q->value = 20.0;
  q->state = FULL;

  return q;
}

void fifo_free(Fifo q)
{
	free(q->mutex);
	free(q->full);
	free(q);
}

/* Definition d'une structure pour le parametre des threads */


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
	printf("Before lock\n");
	pthread_mutex_lock(q->mutex);
	printf("After lock\n");
	printf("Waiting in put\n");
	if(q->state == FULL){
		printf("if passed\n");
		pthread_cond_wait(q->full,q->mutex);
	}
	printf("Done waiting in put\n");
	q->value = t;
	q->state = FULL;
	pthread_cond_signal(q->full);
	pthread_mutex_unlock(q->mutex);
}

/* Retirer un element */
double fifo_get(const Fifo q)
{
	pthread_mutex_lock(q->mutex);
	while(q->state == EMPTY){
		printf("Waiting in get\n");
		pthread_cond_wait(q->full,q->mutex);
	}
	q->state = EMPTY;
	printf("Done waiting in get\n");
	pthread_cond_signal(q->full);
	pthread_mutex_unlock(q->mutex);
	return q->value;
}

/* Impression de la fifo */
void fifo_print(const Fifo q)
{
	//if(q->state == EMPTY)
		printf("{Fifo [value=%f]} \n", q->value);
	/*else
		printf("{Fifo [state=FULL, value=%f]} \n", q->value);*/
}

double calcul(){
	printf("je fais semblant de calculer\n");
	return 20.0;
}

void *boucleCalculGeneral(void *arg){
	
	double value;

	//TODO mettre l'iteration max
	Param_Fifo *a = ((Param_Fifo *) arg);
	for(;;) {
		printf("Début de boucle\n");

		//if(!fifo_is_empty(Q)) {
			/* Debut du calcul */
			value = calcul(value,fifo_get((*a)->get_avant),fifo_get((*a)->get_apres));
			printf("Putting new value1\n");
			fifo_put((*a)->put_avant,value);
			printf("Putting new value2\n");
			fifo_put((*a)->put_apres,value);
			printf("fifo get_avant ");fifo_print((*a)->get_avant);
			printf("fifo get_apres ");fifo_print((*a)->get_apres);
			printf("fifo put_avant ");fifo_print((*a)->put_avant);
			printf("fifo put_apres ");fifo_print((*a)->put_apres);
			//fifo_put(Q,i);
		//}
		sleep(1);
	}
	return 0;
}


void *boucleCalculGauche(void *arg){
	double value;

	//TODO mettre l'iteration max
	Param_Fifo *a = ((Param_Fifo *) arg);
	for(;;) {
		printf("Début de boucle\n");

		//if(!fifo_is_empty(Q)) {
			/* Debut du calcul */
			value = calcul(value,110.0,fifo_get((*a)->get_apres));
			printf("Putting new value  GAuche\n");
			fifo_put((*a)->put_apres,value);
			printf("fifo get_avant ");fifo_print((*a)->get_avant);
			printf("fifo get_apres ");fifo_print((*a)->get_apres);
			printf("fifo put_avant ");fifo_print((*a)->put_avant);
			printf("fifo put_apres ");fifo_print((*a)->put_apres);
			//fifo_put(Q,i);
		//}
		sleep(1);
	}
	return 0;
}

void *boucleCalculDroite(void *arg){
	double value;

	//TODO mettre l'iteration max
	Param_Fifo *a = ((Param_Fifo *) arg);
	for(;;) {
		printf("Début de boucle\n");

		//if(!fifo_is_empty(Q)) {
			/* Debut du calcul */
			value = calcul(value,fifo_get((*a)->get_avant),20.0);
			printf("Putting new value DROITE\n");
			fifo_put((*a)->put_avant,value);
			printf("fifo get_avant ");fifo_print((*a)->get_avant);
			printf("fifo get_apres ");fifo_print((*a)->get_apres);
			printf("fifo put_avant ");fifo_print((*a)->put_avant);
			printf("fifo put_apres ");fifo_print((*a)->put_apres);
			//fifo_put(Q,i);
		//}
		sleep(1);
	}
	return 0;
}
void init_constantes(){
	constanteMur = (0.84 * DT) / (1400 * 840 * DX * DX);
    constanteIso = (0.04 * DT) / (30 * 900 * DX * DX);
}

void *test_put(void *arg){
	Fifo *a = ((Fifo *) arg);
	printf("FIFO PUT\n");
	fifo_print((*a));
	printf("putting ....\n");
	fifo_put((*a),23.0);
	fifo_print((*a));
	return 0;
}
void *test_get(void *arg){
	Fifo *a = ((Fifo *) arg);
	printf("FIFO GET\n");
	fifo_print((*a));
	printf("getting ....\n");
	fifo_get((*a));
	fifo_print((*a));
	return 0;
}
int main(){
	init_constantes();
	
	/* initialisation des threads et des différentes fifos */
	pthread_t *threads[5];
	Param_Fifo *list_param[5];
	Fifo *put_avant,*get_avant;
	pthread_t first;
	Param_Fifo first_param = malloc(sizeof(struct def_param));
	first_param->get_avant = fifo_allocate();
	first_param->get_apres = fifo_allocate();
	first_param->put_apres = fifo_allocate();
	first_param->put_avant = fifo_allocate();
	put_avant = &first_param->get_apres;
	get_avant = &first_param->put_apres;
	threads[0] = &first;
	pthread_create(&first, NULL, boucleCalculGauche, &first_param);
	
	for(int i=1;i<6;i++){
		pthread_t th;
		threads[i] = &th;
		Param_Fifo param = malloc(sizeof(struct def_param));
		param->get_avant = *get_avant;
		param->put_avant = *put_avant;
		param->get_apres = fifo_allocate();
		param->put_apres = fifo_allocate();
		pthread_create(threads[i], NULL, boucleCalculGeneral, &param);
		put_avant = &param->get_apres;
		get_avant = &param->put_apres;
		list_param[i] = &param;				
	}
	
	pthread_t last;
	Param_Fifo last_param = malloc(sizeof(struct def_param));
	last_param->get_avant = *get_avant;
	last_param->put_avant = *put_avant;
	last_param->put_apres = fifo_allocate();
	last_param->get_apres = fifo_allocate();
	threads[6] = &last;
	pthread_create(&last, NULL, boucleCalculDroite, &last_param);
	
	// attente du pere
	for(int i=0;i<7;i++){
		pthread_join(*threads[i], NULL);
	}
	
	// liberation de la mémoire
	free(first_param);
	free(last_param);
	for(int i=0;i<5;i++){
		param_free(*list_param[i]);	
	}/*
	pthread_t th1,th2;
	Fifo fifo1 = fifo_allocate();
	
	pthread_create(&th1, NULL, test_get, &fifo1);
	pthread_create(&th2, NULL, test_put, &fifo1);
	
	pthread_join(th1,NULL);
	pthread_join(th2,NULL);*/

	

	return 0;
}
