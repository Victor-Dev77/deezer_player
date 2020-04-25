package fr.esgi.deezerplayer.view.albumlist

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.api.DeezerAPI
import fr.esgi.deezerplayer.data.repositories.AlbumsRepository
import fr.esgi.deezerplayer.view.albumlist.viewmodel.AlbumListViewModel
import fr.esgi.deezerplayer.view.albumlist.viewmodel.AlbumListViewModelFactory
import kotlinx.android.synthetic.main.album_list_fragment.*

class AlbumListFragment : Fragment() {

    // Factory = design pattern: delegue creation classe ViewModel par une autre classe
    private lateinit var factory: AlbumListViewModelFactory
    private lateinit var viewModel: AlbumListViewModel // connecte vue au viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Ligne par défaut mais peut pas utiliser car AlbumListViewModel prend repository en parametre
        // Et peut pas ajouter de param avec cette ligne donc creer Factory
        // viewModel = ViewModelProviders.of(this).get(AlbumListViewModel::class.java)

        // on initialise TOUT ici !
        // ce n'est pas forcement bon car mtn la vue est connecté a API, Repository et factory
        // on perd la séparation de la vue et la logic metier...

        // le mieux est d'encapsuler tout ca en utilisant une INJECTION de dépendances (possible d'utiliser la lib Dagger2)
        // On creer le viewModel avec la factory class et on créer la factory class avec l'injection class
        // lien: https://openclassrooms.com/fr/courses/4568746-gerez-vos-donnees-localement-pour-avoir-une-application-100-hors-ligne/5106570-architecturez-votre-application-a-laide-dun-viewmodel
        val api = DeezerAPI()
        val repository = AlbumsRepository(api)
        factory = AlbumListViewModelFactory(repository)
        viewModel = ViewModelProviders.of(this, factory).get(AlbumListViewModel::class.java)


        // Recupère les albums (api request)
        viewModel.getAlbums()

        // liveData observable => gere le cycle de vie et protege les données
        // ex: rotation de l'ecran, recevoir un appel, perte de batterie, les données sont pas perdus et recréer grace a LiveData
        viewModel.albums.observe(viewLifecycleOwner, Observer { albums ->
            // album_rcv = id recyclerview dans XML <=> comme findviewbyid
            album_rcv.also {
                // init recyclerview
                it.layoutManager = GridLayoutManager(requireContext(), 3)
                it.addItemDecoration(SpaceGrid(3, 30, true))
                /*albumsRecyclerView.addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                )*/
                it.setHasFixedSize(true)
                it.adapter = AlbumAdapter(albums)
            }
        })

    }

    // class de design pour espacer les cellules, on peut la modifier
    private inner class SpaceGrid(private val mSpanCount: Int, private val mSpacing: Int, private val mIncludeEdge: Boolean) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % mSpanCount
            if (mIncludeEdge) {
                outRect.left = mSpacing - column * mSpacing / mSpanCount
                outRect.right = (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
                outRect.bottom = mSpacing
            } else {
                outRect.left = column * mSpacing / mSpanCount
                outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
                if (position < mSpanCount) {
                    outRect.top = mSpacing
                }
            }
        }
    }

}

