package com.example.collegeproject.base.home.filterFAB.filter

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.collegeproject.R
import com.example.collegeproject.base.BaseActivity
import com.example.collegeproject.base.home.HomeFragment.filterModel
import com.example.collegeproject.base.home.filterFAB.filter.utils.bindOptionalViews
import com.example.collegeproject.base.home.filterFAB.filter.utils.bindView
import com.example.collegeproject.base.home.filterFAB.filter.utils.getValueAnimator
import com.example.collegeproject.base.home.filterFAB.tabs.category.Adapter
import com.example.collegeproject.helper.StaticVariables
import com.example.collegeproject.models.CategoryModel
import com.example.collegeproject.roomDB.TaskRunner
import com.example.collegeproject.roomDB.userCities.DbMethods
import com.example.collegeproject.viewModel.RoomViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.libizo.CustomEditText
import java.lang.ref.WeakReference
import java.util.*
import java.util.Observer
import kotlin.collections.ArrayList

/**
 * ViewPager adapter to display all the filters
 */
class FiltersPagerAdapter(private val context: Context, private val listener: (updatedPosition: Int, selectedMap: Map<Int, List<Int>>) -> Unit)
    : RecyclerView.Adapter<FiltersPagerAdapter.FiltersPagerViewHolder>(), Adapter.itemClick, com.example.collegeproject.base.home.filterFAB.tabs.venue.Adapter.itemClick {

    private val toggleAnimDuration = context.resources.getInteger(R.integer.toggleAnimDuration).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var selectedMap = mutableMapOf<Int, MutableList<Int>>()
    private var date = Calendar.getInstance()
    private var allCategories: ArrayList<CategoryModel>? = ArrayList()
    private var selectedIds: ArrayList<String>? = ArrayList()
    private var cityHistory: ArrayList<String>? = ArrayList()
    private var citySelected: ArrayList<String>? = ArrayList()
    private var roomViewModel: RoomViewModel = ViewModelProvider((context as ViewModelStoreOwner)).get(RoomViewModel::class.java)
    private var adapter: Adapter? = null
    private var cityAdapter: com.example.collegeproject.base.home.filterFAB.tabs.venue.Adapter? = null
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filter(s.toString())
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = FiltersLayout.numTabs

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> R.layout.filter_date
        1 -> R.layout.filter_city
        else -> R.layout.filter_categories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersPagerViewHolder =
            FiltersPagerViewHolder(inflater.inflate(viewType, parent, false))

    override fun onBindViewHolder(holder: FiltersPagerViewHolder, position: Int) {
        val selectedList = selectedMap.getOrPut(position) { mutableListOf() }

        holder.date.forEachIndexed { index: Int, view: LinearLayout ->
            view.setOnClickListener {
                val isToggled = selectedList.contains(index)
                when (index) {
                    0 -> {
                        if (isToggled) {
                            selectedList -= 0
                            filterModel.date = 0
                        } else {
                            selectedList += 0
                            selectedList -= 1
                            selectedList -= 2
                            holder.date[1].setBackgroundResource(R.drawable.ic_pill)
                            holder.date[2].setBackgroundResource(R.drawable.ic_pill)
                            holder.customTV.text = "CUSTOM"
                            date = Calendar.getInstance()
                            filterModel.date = date.timeInMillis
                        }
                        val toggleAnimator = getValueAnimator(!isToggled,
                                toggleAnimDuration, DecelerateInterpolator()) {
                            if (isToggled)
                                view.setBackgroundResource(R.drawable.ic_pill)
                            else
                                view.setBackgroundResource(R.drawable.ic_pill_selected)
                        }
                        toggleAnimator.start()
                        listener(position, selectedMap)
                    }
                    1 -> {
                        if (isToggled) {
                            selectedList -= 1
                            filterModel.date = 0
                        } else {
                            selectedList += 1
                            selectedList -= 0
                            selectedList -= 2
                            holder.date[0].setBackgroundResource(R.drawable.ic_pill)
                            holder.date[2].setBackgroundResource(R.drawable.ic_pill)
                            holder.customTV.text = "CUSTOM"
                            date = Calendar.getInstance()
                            date.add(Calendar.DAY_OF_MONTH, 1)
                            filterModel.date = date.timeInMillis
                        }
                        val toggleAnimator = getValueAnimator(!isToggled,
                                toggleAnimDuration, DecelerateInterpolator()) {
                            if (isToggled)
                                view.setBackgroundResource(R.drawable.ic_pill)
                            else
                                view.setBackgroundResource(R.drawable.ic_pill_selected)
                        }
                        toggleAnimator.start()
                        listener(position, selectedMap)
                    }
                    else -> {
                        if (isToggled) {
                            selectedList -= 2
                            holder.customTV.text = "Custom"
                            val toggleAnimator = getValueAnimator(!isToggled,
                                    toggleAnimDuration, DecelerateInterpolator()) {
                                view.setBackgroundResource(R.drawable.ic_pill)
                            }
                            toggleAnimator.start()
                            listener(position, selectedMap)
                            filterModel.date = 0
                        } else {
                            date = Calendar.getInstance()
                            val year = date.get(Calendar.YEAR)
                            val month = date.get(Calendar.MONTH)
                            val day = date.get(Calendar.DAY_OF_MONTH)
                            val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year1, monthOfYear, dayOfMonth ->
                                holder.date[0].setBackgroundResource(R.drawable.ic_pill)
                                holder.date[1].setBackgroundResource(R.drawable.ic_pill)
                                holder.customTV.text = "" + dayOfMonth + " / " + (monthOfYear + 1) + " / " + year
                                selectedList += 2
                                selectedList -= 0
                                selectedList -= 1
                                date.set(Calendar.YEAR, year1)
                                date.set(Calendar.MONTH, monthOfYear)
                                date.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                date.set(Calendar.HOUR, 0)
                                date.set(Calendar.MINUTE, 0)
                                date.set(Calendar.SECOND, 0)
                                val toggleAnimator = getValueAnimator(!isToggled,
                                        toggleAnimDuration, DecelerateInterpolator()) {
                                    view.setBackgroundResource(R.drawable.ic_pill_selected)
                                }
                                toggleAnimator.start()
                                listener(position, selectedMap)
                                filterModel.date = date.timeInMillis
                            }, year, month, day)
                            dpd.show()
                        }
                    }
                }
            }
        }

        holder.recyclerViewCity.forEachIndexed { _: Int, rV: RecyclerView ->
            roomViewModel.userCities.observe((context as LifecycleOwner), androidx.lifecycle.Observer{
                cityHistory?.clear()
                cityHistory?.addAll(it)
                cityAdapter = com.example.collegeproject.base.home.filterFAB.tabs.venue.Adapter(context, cityHistory, citySelected, this)
                rV.adapter = cityAdapter
                rV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            })
        }


        holder.recyclerViewCategory.forEachIndexed { _: Int, rV: RecyclerView ->
            allCategories = ArrayList()
            allCategories?.addAll(StaticVariables.eventCategories)
            selectedIds = ArrayList()
            adapter = Adapter(context, allCategories, selectedIds, this)
            rV.adapter = adapter
            rV.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        holder.categoryET.forEachIndexed { _: Int, et: CustomEditText ->
            et.addTextChangedListener(textWatcher)
            et.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        (context as BaseActivity).closeKeyboard()
                        true
                    }
                    else -> false
                }
            }
        }

        holder.cityET.forEachIndexed { _: Int, et: CustomEditText ->
            val textWatcherCity = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    holder.addCity[0].isEnabled = !s.isNullOrEmpty()
                    filterCity(s.toString())
                }
            }
            et.addTextChangedListener(textWatcherCity)
            et.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        (context as BaseActivity).closeKeyboard()
                        true
                    }
                    else -> false
                }
            }
        }

        holder.addCity.forEachIndexed { _: Int, fab: FloatingActionButton ->
            fab.setOnClickListener {
                val query: String = holder.cityET[0].text.toString()
                roomViewModel.addUserCities(query)
                if (!cityHistory?.contains(query)!!) {
                    cityHistory?.add(0, query)
                }
                citySelected?.clear()
                citySelected?.add(query)
                adapter?.notifyDataSetChanged()
                val isToggled = selectedList.contains(5)
                if (citySelected!!.isNotEmpty() && !isToggled) {
                    filterModel.city = query
                    selectedList += 5
                } else if (citySelected!!.isEmpty()) {
                    filterModel.city = ""
                    selectedList -= 5
                }
                listener(1, selectedMap)
                (context as BaseActivity).closeKeyboard()
                holder.cityET[0].setText("")
            }
        }
    }

    class FiltersPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: List<LinearLayout> by bindOptionalViews(R.id.today, R.id.tomorrow, R.id.custom)
        val customTV: TextView by bindView(R.id.customDate)
        val cityET: List<CustomEditText> by bindOptionalViews(R.id.city)
        val categoryET: List<CustomEditText> by bindOptionalViews(R.id.search)
        val recyclerViewCity: List<RecyclerView> by bindOptionalViews(R.id.cities)
        val recyclerViewCategory: List<RecyclerView> by bindOptionalViews(R.id.categories)
        val addCity: List<FloatingActionButton> by bindOptionalViews(R.id.add)
    }

    private fun Activity.closeKeyboard() {
        closeKeyboard(currentFocus ?: View(this))
    }

    private fun Context.closeKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun itemClicked(model: CategoryModel) {
        if (selectedIds?.contains(model.id)!!) {
            selectedIds!!.remove(model.id)
        } else {
            selectedIds!!.add(model.id!!)
        }
        filterModel.categories = selectedIds
        adapter?.notifyDataSetChanged()

        val selectedList = selectedMap.getOrPut(2) { mutableListOf() }
        val isToggled = selectedList.contains(10)
        if (filterModel.categories.size != 0 && !isToggled) {
            selectedList += 10
        } else if (filterModel.categories.isEmpty()) {
            selectedList -= 10
        }
        listener(2, selectedMap)
    }

    private fun filter(searchWord: String) {
        val filterCategories: ArrayList<CategoryModel> = ArrayList()
        allCategories?.forEachIndexed { _: Int, model: CategoryModel ->
            if (model.name.toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT)))
                filterCategories.add(model)
        }
        adapter?.filter(filterCategories)
    }

    private fun filterCity(searchWord: String) {
        val filterCities: ArrayList<String> = ArrayList()
        cityHistory?.forEachIndexed { _: Int, model: String ->
            if (model.toLowerCase(Locale.ROOT).contains(searchWord.toLowerCase(Locale.ROOT)))
                filterCities.add(model)
        }
        cityAdapter?.filter(filterCities)
    }

    override fun cityClicked(selectedCity: String) {
        if (citySelected?.contains(selectedCity)!!) {
            citySelected?.clear()
            citySelected?.remove(selectedCity)
        } else {
            citySelected?.clear()
            citySelected?.add(selectedCity)
        }
        cityAdapter?.notifyDataSetChanged()
        val selectedList = selectedMap.getOrPut(1) { mutableListOf() }
        val isToggled = selectedList.contains(5)
        if (citySelected!!.isNotEmpty() && !isToggled) {
            filterModel.city = selectedCity
            selectedList += 5
        } else if (citySelected!!.isEmpty()) {
            filterModel.city = ""
            selectedList -= 5
        }
        listener(1, selectedMap)
    }
}
