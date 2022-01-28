package com.example.socialastro.recycler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialastro.Constansts
import com.example.socialastro.R
import com.example.socialastro.extension.formatDateName
import com.example.socialastro.extension.formatNameSite
import com.example.socialastro.model.ModelArticle
import com.example.socialastro.model.TypeButton

class PostAdapter(private val context: Context, private val listArticles: MutableList<ModelArticle>) :
    RecyclerView.Adapter<PostAdapter.PostAdapterViewHolder>(), Filterable {

    private val listFull = mutableListOf<ModelArticle>()
//    init {
//        Log.e("FILTRERLA", "INICIANDO qtd list ${listArticles.size}")
//        listFull.clear()
//        listFull.addAll(listArticles)
//        Log.e("FILTRERLA", "INICIANDO apos ${listFull.size}")
//    }

//    fun setList(list: MutableList<ModelArticle>) {
//        listFull.addAll(list)
//    }

    private lateinit var onClickItem: (item: ModelArticle, type: TypeButton) -> Unit
    fun setOnItemClickListener(callback: (item: ModelArticle, type: TypeButton) -> Unit) {
        this.onClickItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_articles, parent, false)
        return PostAdapterViewHolder(view, onClickItem)
    }

    override fun onBindViewHolder(holder: PostAdapterViewHolder, position: Int) {
        val item = listArticles[position]
        holder.bind(item)
    }

    override fun getItemCount() = listArticles.size

    inner class PostAdapterViewHolder(itemView: View, onClickItem: (item: ModelArticle, type: TypeButton) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val imagePhoto = itemView.findViewById<ImageView>(R.id.imageCardPhoto)
        val buttonFavorite = itemView.findViewById<ImageView>(R.id.itemFavoriteButton)
        val buttonAbrirLink = itemView.findViewById<TextView>(R.id.buttonAbrirLink)
        val nameSite = itemView.findViewById<TextView>(R.id.itemNameSite)
        val descriptionPost = itemView.findViewById<TextView>(R.id.itemDescriptionPost)
        val imagePost = itemView.findViewById<ImageView>(R.id.itemImagePost)
        val url = itemView.findViewById<TextView>(R.id.itemUrl)
        val date = itemView.findViewById<TextView>(R.id.itemPostDate)
        val title = itemView.findViewById<TextView>(R.id.itemNameTitle)

        fun bind(item: ModelArticle) {
            imagePhoto.setImageResource(whatsImage(item))
            buttonFavorite.setOnClickListener {
                onClickItem(item, TypeButton.FAVORITES)
            }
            buttonAbrirLink.setOnClickListener {
                onClickItem(item, TypeButton.ABRIR_LINK)
            }

            if (item.featured) {
                buttonFavorite.setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                buttonFavorite.setColorFilter(ContextCompat.getColor(context, R.color.black))
            }
            nameSite.text = item.newsSite
            descriptionPost.text = item.summary
            url.text = item.url.formatNameSite()
            date.text = item.publishedAt.formatDateName()
            title.text = item.title
            Glide.with(itemView)
                .load(item.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.loading_animation)
                //.error(R.drawable.ic_broken_image)
                //.fallback(R.drawable.ic_no_image)
                .into(imagePost)
        }


    }

    fun whatsImage(post: ModelArticle): Int {
        return when (post.newsSite) {
            Constansts.NASA -> {
                R.drawable.nasa
            }
            Constansts.NASA_SPACEFLIGHT -> {
                R.drawable.nasa_spaceflight
            }
            Constansts.ARSTECHNICA -> {
                R.drawable.arstechnica
            }
            Constansts.SPACEFLIGHT_NOW -> {
                R.drawable.spaceflight
            }
            Constansts.SPACENEWS -> {
                R.drawable.spacenews
            }
            Constansts.TESLARATI -> {
                R.drawable.tesla
            }
            else -> {
                R.drawable.ic_android_black
            }
        }

    }

    override fun getFilter(): Filter {
        return listFilter
    }

    fun setList(list: MutableList<ModelArticle>) {
        Log.e("FILTRERLA", "INICIANDO dd qtd list ${list.size}")
        listFull.clear()
        listFull.addAll(list)
    }

    private val listFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            Log.e("FILTRERLA", "lisFill ${listFull.size}")
            Log.e("FILTRERLA", "listFilter: $constraint")
            val filteredList = mutableListOf<ModelArticle>()
            if (constraint == null || constraint.isEmpty()) {
                Log.e("FILTRERLA", "listFilter: vazio e listFull $listFull")
                filteredList.addAll(listFull)
            } else {
                val filter = constraint.toString().toLowerCase().trim()
                for (item in listFull) {
                    if (item.newsSite.toLowerCase().contains(filter)
                        || item.summary.toLowerCase().contains(filter)
                        || item.title.toLowerCase().contains(filter)
                        || item.publishedAt.toLowerCase().contains(filter)
                    ) {
                        filteredList.add(item)
                    }
                }
                Log.e("FILTRERLA", "nao vazio ${listArticles.size} $listArticles")
            }

            val results = FilterResults()
            results.values = filteredList
            Log.e("FILTRERLA", "listFilter results: ${results.values} ${results.count} $results")
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            Log.e("FILTRERLA", "Publish results ${results?.values} ${results?.count}")
            Log.e("FILTRERLA", "Publish constraint ${constraint.toString()}")
            listArticles.clear()
            results?.values?.let {
                listArticles.addAll(it as MutableList<ModelArticle>)
            }

            notifyDataSetChanged()
        }

    }
}