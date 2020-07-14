package fr.esgi.deezerplayer.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// object abstrait executant une coroutine (fonction async) prenant en param la fonc async (work)
// et un callback une fois la fonction finie
object Coroutines {

    fun <T : Any> ioThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
        // cette ligne créer la coroutine et la lance sur le Thread Main
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async rt@{
                return@rt work()
            }.await()
            callback(data)
        }


    /*
        Dispatchers.Main -
        Utilisez ce répartiteur pour exécuter une coroutine sur le fil principal Android.
        Cela doit être utilisé uniquement pour interagir avec l'interface utilisateur et effectuer un travail rapide.
        Les exemples incluent l'appel de suspend fonctions, l'exécution d'opérations de structure d'interface utilisateur Android
        et la mise à jour d' LiveData objets.


        Dispatchers.IO -
        Ce répartiteur est optimisé pour effectuer des E / S disque ou réseau en dehors du thread principal.
        Les exemples incluent l'utilisation du composant Room,
        la lecture ou l'écriture dans des fichiers et l'exécution de toute opération réseau.


        Dispatchers.Default -
        Ce répartiteur est optimisé pour effectuer un travail gourmand en CPU en dehors du thread principal.
        Les exemples d'utilisation incluent le tri d'une liste et l'analyse JSON
     */
}