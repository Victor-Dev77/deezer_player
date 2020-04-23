package fr.esgi.deezerplayer

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.esgi.deezerplayer.data.api.DeezerProvider
import fr.esgi.deezerplayer.data.api.Listener
import fr.esgi.deezerplayer.model.Album
import fr.esgi.deezerplayer.recycler.AlbumAdapter
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var albumsRecyclerView: RecyclerView
    private val albumsAdapter: AlbumAdapter by lazy { AlbumAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        albumsRecyclerView = findViewById(R.id.album_rcv)

        initRecyclerView()

        getAlbums()
    }

    private fun initRecyclerView() {
        albumsRecyclerView.layoutManager = GridLayoutManager(this, 3)
        albumsRecyclerView.addItemDecoration(SpaceGrid(3, 30, true))
        albumsRecyclerView.adapter = albumsAdapter
        /*albumsRecyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )*/
    }

    private fun getAlbums() {
        DeezerProvider.getAlbums(object : Listener<List<Album>> {
            override fun onSuccess(data: List<Album>) {
                albumsAdapter.data = data
            }

            override fun onError(t: Throwable) {
                Toast.makeText(applicationContext, t.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }


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
