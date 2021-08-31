package com.adwi.pexwallpapers.ui.setwallpaper.bottomsheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.adwi.pexwallpapers.data.local.entity.Wallpaper
import com.adwi.pexwallpapers.data.repository.interfaces.WallpaperRepositoryInterface
import com.adwi.pexwallpapers.shared.base.BaseViewModel
import com.adwi.pexwallpapers.util.onIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BottomSheetViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val wallpaperRepository: WallpaperRepositoryInterface
) : BaseViewModel() {

    private var savedCategoryName: String? = null

    private val categoryName = MutableStateFlow<String?>(null)

    val wallpaperResults = categoryName.flatMapLatest { categoryName ->
        categoryName?.let {
            wallpaperRepository.getWallpapersOfCategory(categoryName)
        } ?: emptyFlow()
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        savedCategoryName = savedStateHandle.get(LAST_CATEGORY_NAME)
        if (savedCategoryName.isNullOrBlank()) {
            categoryName.value = savedCategoryName
        }
    }

    fun onCategoryNameSubmit(categoryName: String) {
        this.categoryName.value = categoryName
        savedStateHandle.set(LAST_CATEGORY_NAME, categoryName)
    }

    fun onFavoriteClick(wallpaper: Wallpaper) {
        val isFavorite = wallpaper.isFavorite
        wallpaper.isFavorite = !isFavorite
        onIO {
            wallpaperRepository.updateWallpaper(wallpaper)
        }
    }

    companion object {
        private const val LAST_CATEGORY_NAME = "LAST_CATEGORY_NAME"
    }
}