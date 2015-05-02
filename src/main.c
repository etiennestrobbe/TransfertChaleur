#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include "util.h"


double DT = 600.0;
double DX = 0.04;
double C1;
double C2;
double matrice[100][7];
int found = 0;

/* Methode qui fait le calcul des nouvelles temperatures */
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

/* Methode qui fait le calcul pour les threads internes */
void *boucleCalculGeneral(void *arg){
	double value = 20.0;
	int it=0;
	Param_Fifo a = ((Param_Fifo ) arg);
	while(it++< a->iteration) {
		double b = fifo_get((a)->lire_gauche);
		double c = fifo_get((a)->lire_droite);
		value = calcul(value,b,c,a->id,it);
		fifo_put((a)->ecrire_gauche,value);
		fifo_put((a)->ecrire_droite,value);
	}
	return 0;
}

/* Methode qui fait le calcul pour le thread externe gauche */
void *boucleCalculGauche(void *arg){
	double value = 20.0;
	int it = 0;
	Param_Fifo a = ((Param_Fifo) arg);
	while(it++< a->iteration) {
		value = calcul(value,110.0,fifo_get((a)->lire_droite),a->id,it);
		fifo_put((a)->ecrire_droite,value);
	}
	return 0;
}

/* Methode qui fait le calcul pour le thread externe droite */
void *boucleCalculDroite(void *arg){
	double value = 20.0;
	int it=0;
	Param_Fifo a = ((Param_Fifo ) arg);
	while(it++< a->iteration) {
		if(found == 0){
			if(value >  21.0){
				found = 1;
				printf("Température changée apres %d itérations (~%f heures)\n",it,(it*DT)/3600.0);
			}
		}
		value = calcul(value,fifo_get((a)->lire_gauche),20.0,a->id,it);
		fifo_put((a)->ecrire_gauche,value);
	}
	return 0;
}
void init_constantes(){
	C1 = (0.84 * DT) / (1400 * 840 * DX * DX);
    C2 = (0.04 * DT) / (30 * 900 * DX * DX);
    printf("Les constantes : C1=%f | C2=%f\n",C1,C2);
}

void init_threads(){
	
}


int main(){
	init_constantes();
	int it = 100;
	printf("DT=%f, DX=%f, nb iterations=%d\n",DT,DX,it);
	float temps;
    clock_t t1, t2;
	
	/* initialisation des threads et des différentes fifos */
	
	t1 = clock();
	pthread_t *threads[7];
	Param_Fifo list_param[7];
	Fifo *ecrire_gauche,*lire_gauche;
	/* Premier thread (celui correspondant a la tranche, que l'on calcul, la plus a gauche) */
	pthread_t first;
	Param_Fifo first_param = malloc(sizeof(struct def_param));
	first_param->lire_droite = fifo_allocate();
	first_param->ecrire_droite = fifo_allocate();
	ecrire_gauche = &first_param->lire_droite;
	lire_gauche = &first_param->ecrire_droite;
	threads[0] = &first;
	first_param->iteration = it;
	first_param->id = 0;
	list_param[0] = first_param;
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
		list_param[i] = param;				
	}
	/* Dernier thread (celui correspondant a la tranche, que l'on calcul, la plus a droite) */
	pthread_t last;
	Param_Fifo last_param = malloc(sizeof(struct def_param));
	last_param->lire_gauche = *lire_gauche;
	last_param->ecrire_gauche = *ecrire_gauche;
	threads[6] = &last;
	last_param->iteration = it;
	last_param->id = 6;
	list_param[6] = last_param;
	pthread_create(&last, NULL, boucleCalculDroite, last_param);
	
	for(int i=0;i<7;i++){
		pthread_join(*threads[i], NULL);
	}
	t2 = clock();
	
	// liberation de la mémoire
	for(int i=0;i<7;i++){
		param_free(list_param[i]);
	}
	printf("[ 0 : 110 20 20 20 20 20 - 20 20 20 20 ]\n");
	for(int i=1;i<it;i++){
		printf("[ %d : 110 ",i);
		for(int j=0;j<7;j++){
			if(j == 4) printf("%d - ",(int)matrice[i][j]);
			printf("%d ",(int)matrice[i][j]);
		}
		printf("20 ]\n");
	}
	
	temps = (float)(t2-t1)/CLOCKS_PER_SEC;
    printf("temps d'execution = %f sec.\n", temps);
	return 0;
}
