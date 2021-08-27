package com.adwi.pexwallpapers.ui.favorites

import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adwi.pexwallpapers.R
import com.adwi.pexwallpapers.databinding.FragmentFavoritesBinding
import com.adwi.pexwallpapers.shared.adapter.WallpaperListAdapter
import com.adwi.pexwallpapers.shared.base.BaseFragment
import com.adwi.pexwallpapers.util.launchCoroutine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FavoritesFragment :
    BaseFragment<FragmentFavoritesBinding, WallpaperListAdapter>(
        inflate = FragmentFavoritesBinding::inflate
    ) {

    override val viewModel: FavoritesViewModel by viewModels()

    override fun setupToolbar() {
        binding.toolbarLayout.apply {
            titleTextView.text = getString(R.string.favorites)
            backButton.isVisible = false
        }
    }

    override fun setupAdapters() {
        mAdapter = WallpaperListAdapter(
            onItemClick = { wallpaper ->
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToPreviewFragment(
                        wallpaper
                    )
                )
            },
            onItemLongClick = { wallpaper ->
                viewModel.onFavoriteClick(wallpaper)
            },
            itemRandomHeight = false
        )
    }

    override fun setupViews() {
        setHasOptionsMenu(true)
        binding.apply {
            recyclerView.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    override fun setupListeners() {
        binding.toolbarLayout.apply {
            menuButton.setOnClickListener {
                showMenu(it, R.menu.menu_wallpapers)
            }
        }
    }

    override fun setupFlows() {
        binding.apply {
            launchCoroutine {
                viewModel.favorites.collect {
                    val favorites = it ?: return@collect
                    mAdapter?.submitList(favorites)
                    noFavoritesTextview.isVisible = favorites.isEmpty()
                    recyclerView.isVisible = favorites.isNotEmpty()
                }
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete_all_favorites -> {
                viewModel.onDeleteAllFavorites()
                true
            }
            else -> false
        }
    }
}