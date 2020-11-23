package com.example.collegeproject.base.home.filterFAB.filter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.collegeproject.R
import com.example.collegeproject.base.home.filterFAB.filter.utils.bindColor
import com.example.collegeproject.base.home.filterFAB.filter.utils.bindDimen
import com.example.collegeproject.base.home.filterFAB.filter.utils.blendColors
import com.example.collegeproject.viewModel.RoomViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * [FiltersLayout] and [FiltersMotionLayout] both use the same ViewPager2 and tab setup, hence
 * we abstracted away those functionalities here.
 *
 * This class is responsible for setting up the viewpager and tabs, syncing them and keeping track
 * of active filters
 */
@SuppressLint("WrongConstant")
class ViewPagerTabsHandler(
        private val viewPager: ViewPager2,
        private val tabLayout: TabLayout,
        private val bottomBarCardView: CardView
) {

    private val context = viewPager.context
    private val bottomBarColor: Int by bindColor(context, R.color.bottom_bar_color)
    private val bottomBarSelectedColor: Int by bindColor(context, R.color.bottom_bar_selected_color)
    private val tabColor: Int by bindColor(context, R.color.tab_unselected_color)
    private val tabSelectedColor: Int by bindColor(context, R.color.tab_selected_color)

    private val tabItemWidth: Float by bindDimen(context, R.dimen.tab_item_width)
    private val filterLayoutPadding: Float by bindDimen(context, R.dimen.filter_layout_padding)

    private val toggleAnimDuration = context.resources.getInteger(R.integer.toggleAnimDuration).toLong()

    private var totalTabsScroll = 0
    var hasActiveFilters = false
        private set

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    fun init() {
        // ViewPager & Tabs
        viewPager.offscreenPageLimit = FiltersLayout.numTabs
    }

    /**
     * Used to set tab and view pager adapters and remove them when unnecessary.
     * This is done because keeping the adapters around when fab is never clicked
     * or when fab is collapsed is wasteful.
     */
    fun setAdapters(set: Boolean) {
        if (set) {
            viewPager.adapter = FiltersPagerAdapter(context!!, ::onFilterSelected)
            TabLayoutMediator(tabLayout, viewPager,
                    TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                        when (position) {
                            0 -> {
                                tab.text = "Time"
                                tab.removeBadge()
                            }
                            1 -> {
                                tab.text = "Venue"
                                tab.removeBadge()
                            }
                            2 -> {
                                tab.text = "Category"
                                tab.removeBadge()
                            }
                        }
                    }).attach()
        } else {
            viewPager.adapter = null
            hasActiveFilters = false
            totalTabsScroll = 0
        }
    }

    /**
     * Callback method for [FiltersPagerAdapter]. When ever a filter is selected, adapter will call this function.
     * Animates the bottom bar to pink if there are any active filters and vice versa
     */
    private fun onFilterSelected(updatedPosition: Int, selectedMap: Map<Int, List<Int>>) {
        val hasActiveFilters = selectedMap.filterValues { it.isNotEmpty() }.isNotEmpty()
        val bottomBarAnimator =
                if (hasActiveFilters && !this.hasActiveFilters) ValueAnimator.ofFloat(0f, 1f)
                else if (!hasActiveFilters && this.hasActiveFilters) ValueAnimator.ofFloat(1f, 0f)
                else null

        tabLayout.getTabAt(updatedPosition)?.removeBadge()
        if (!selectedMap[updatedPosition].isNullOrEmpty()) {
            val badge: BadgeDrawable? = tabLayout.getTabAt(updatedPosition)?.orCreateBadge
            badge?.backgroundColor = ContextCompat.getColor(context, R.color.tab_badge_color)
        }

        bottomBarAnimator?.let {
            this.hasActiveFilters = !this.hasActiveFilters
            it.addUpdateListener { animation ->
                val color = blendColors(bottomBarColor, bottomBarSelectedColor, animation.animatedValue as Float)
                bottomBarCardView.setCardBackgroundColor(color)
            }
            it.duration = toggleAnimDuration
            it.start()
        }
    }
}