/*SGC - Sistema Gestione Corsi (di lingua inglese)*/

import controller.ApplicationController;

public class Main {
    public static void main(String[] args) {
        ApplicationController applicationController = new ApplicationController();
        applicationController.start();
    }
}

// DA CONTROLLARE TUTTO IL CODICE!!!

// aggiungi ON DELETE CASCADE a tutte le entità che una volta cancellate causano "buchi" nel database
/*
* ON DELETE CASCADE sulla rimozione di corsi
* ON DELETE CASCADE sulla rimozione di studenti
* ... ... ... ...
*
* */

//aggiungere i costruttori completi (quelli da usare quando prendi dal DB)

//ricontrollato alcune funzioni, logicamente ci siamo,
//da vedere bene (in realtà proprio da scegliere) come sono le tabelle nel DB cosi da uniformare tutte le query SQL

//scegliere una lingua per mostrare tutte le view, e.g. solo italiano o solo inglese

/* una volta controllato il codice, sistemarlo organizzando bene anche spazialemtne le funzioni */

/* Se c'è tempo andare a fare una sola connessione verso il DB che cambia dinamicamente in base a chi la chiama */
/* Analogamente si dovrebbe fare per bufferedReader, ne basta uno che viene passato tra le funzioni che lo necessitano (tecnicamente solo nella view) */
/* ricodarsi di deallocare risorse come connessioni e reader alla chiusura */