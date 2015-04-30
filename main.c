#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>


/* Definition de la FIFO */
struct def_fifo{
	pthread_cond_t *condition;
	pthread_mutex_t *mutex;
	enum {EMPTY,FULL} state;
	double value;
	int id;
};

typedef struct def_fifo *Fifo;

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

/* Definition d'une structure pour le parametre des threads */


double DT = 600.0;
double DX = 0.04;
double C1;
double C2;
double matrice[100000][7];

struct def_param{
	Fifo lire_gauche;
	Fifo lire_droite;
	Fifo ecrire_droite;
	Fifo ecrire_gauche;
	int iteration;
	int id;
};

typedef struct def_param *Param_Fifo;

void param_free(Param_Fifo param){
	free(param->lire_gauche);
	free(param->lire_droite);
	free(param->ecrire_droite);
	free(param->ecrire_gauche);
	free(param);
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

double calcul(double actuelle, double avant, double apres, int pos,int it){
	/* 3 cas possible */
	double nouvelle;
	/* 1er cas i=0,1,2,3 */
	if(pos <=3){
		nouvelle = actuelle + C1*(avant + apres - 2*actuelle);
	}
	/* 2em cas i=4 */
	if(pos ==4){
		nouvelle = actuelle + C1*(avant-actuelle) + C2*(apres-actuelle);
	}
	/* 3em cas i=5,6 */
	if(pos > 4){
		nouvelle = actuelle + C2*(avant + apres - 2*actuelle);
	}
	matrice[it][pos] = nouvelle;
	return nouvelle;
}

void *boucleCalculGeneral(void *arg){
	
	double value;
	int it=0;

	//TODO mettre l'iteration max
	Param_Fifo a = ((Param_Fifo ) arg);
	while(it< a->iteration) {

		/* Debut du calcul */
		double b = fifo_get((a)->lire_gauche);
		double c = fifo_get((a)->lire_droite);
		
		value = calcul(value,b,c,a->id,it);
		fifo_put((a)->ecrire_gauche,value);
		fifo_put((a)->ecrire_droite,value);
		it++;
	}
	return 0;
}


void *boucleCalculGauche(void *arg){
	double value;
	int it = 0;

	//TODO mettre l'iteration max
	Param_Fifo a = ((Param_Fifo) arg);
	while(it< a->iteration) {
		/* Debut du calcul */
		value = calcul(value,110.0,fifo_get((a)->lire_droite),a->id,it);
		fifo_put((a)->ecrire_droite,value);
		it++;
	}
	return 0;
}

void *boucleCalculDroite(void *arg){
	double value;
	int it=0;

	//TODO mettre l'iteration max
	Param_Fifo a = ((Param_Fifo ) arg);
	while(it< a->iteration) {
		/* Debut du calcul */
		value = calcul(value,fifo_get((a)->lire_gauche),20.0,a->id,it);
		fifo_put((a)->ecrire_gauche,value);
		it++;
	}
	return 0;
}
void init_constantes(){
	C1 = (0.84 * DT) / (1400 * 840 * DX * DX);
    C2 = (0.04 * DT) / (30 * 900 * DX * DX);
    printf("Les constantes : C1=%f | C2=%f\n",C1,C2);
}


int main(){
	init_constantes();
	int it = 100000;
	float temps;
    clock_t t1, t2;
	
	
	
	/* initialisation des threads et des différentes fifos */
	
	t1 = clock();
	pthread_t *threads[7];
	Param_Fifo *list_param[5];
	Fifo *ecrire_gauche,*lire_gauche;
	pthread_t first;
	Param_Fifo first_param = malloc(sizeof(struct def_param));
	first_param->lire_droite = fifo_allocate();
	first_param->ecrire_droite = fifo_allocate();
	ecrire_gauche = &first_param->lire_droite;
	lire_gauche = &first_param->ecrire_droite;
	threads[0] = &first;
	first_param->iteration = it;
	first_param->id = 0;
	pthread_create(&first, NULL, boucleCalculGauche, first_param);
	
	for(int i=1;i<6;i++){
		pthread_t th;
		threads[i] = &th;
		Param_Fifo param = malloc(sizeof(struct def_param));
		param->lire_gauche = *lire_gauche;
		param->ecrire_gauche = *ecrire_gauche;
		param->lire_droite = fifo_allocate();
		param->ecrire_droite = fifo_allocate();
		param->iteration = it; 
		param->id = i;
		pthread_create(threads[i], NULL, boucleCalculGeneral, param);
		ecrire_gauche = &param->lire_droite;
		lire_gauche = &param->ecrire_droite;
		list_param[i-1] = &param;				
	}
	
	pthread_t last;
	Param_Fifo last_param = malloc(sizeof(struct def_param));
	last_param->lire_gauche = *lire_gauche;
	last_param->ecrire_gauche = *ecrire_gauche;
	threads[6] = &last;
	last_param->iteration = it;
	last_param->id = 6;
	pthread_create(&last, NULL, boucleCalculDroite, last_param);
	
	// attente du pere
	for(int i=0;i<7;i++){
		pthread_join(*threads[i], NULL);
	}
	t2 = clock();
	
	// liberation de la mémoire
	free(first_param->lire_droite);
	free(first_param->ecrire_droite);
	free(first_param);
	free(last_param);
	for(int i=0;i<5;i++){
		//param_free(*list_param[i]);
	}/*
	pthread_t th1,th2;
	Fifo fifo1 = fifo_allocate();
	
	pthread_create(&th1, NULL, test_get, &fifo1);
	pthread_create(&th2, NULL, test_put, &fifo1);
	
	pthread_join(th1,NULL);
	pthread_join(th2,NULL);*/

	for(int i=0;i<it;i++){
		printf("[ 110 ");
		for(int j=0;j<7;j++){
			if(j == 4) printf("%d - ",(int)matrice[i][j]);
			printf("%d ",(int)matrice[i][j]);
		}
		printf("20 ]\n");
	}
	
	temps = (float)(t2-t1)/CLOCKS_PER_SEC;
    printf("temps = %f\n", temps);
	return 0;
}
