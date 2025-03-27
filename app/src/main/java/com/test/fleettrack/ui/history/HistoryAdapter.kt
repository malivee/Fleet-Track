package com.test.fleettrack.ui.history

import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.fleettrack.data.VehicleStatusEntity
import com.test.fleettrack.databinding.CardHistoryBinding

class HistoryAdapter :
    ListAdapter<VehicleStatusEntity, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: CardHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(status: VehicleStatusEntity) {
            binding.itemName.text = status.id.toString()
            binding.itemDesc.text = if (status.doorStatus || status.engineStatus) {
                "There is nothing wrong with this data"
            } else {
                """
                Engine Status: Off
                Door Status: Open
            """.trimIndent()

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CardHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val viewHolderItem = getItem(position)
        holder.bindItem(viewHolderItem)

    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<VehicleStatusEntity> =
            object : DiffUtil.ItemCallback<VehicleStatusEntity>() {
                override fun areItemsTheSame(
                    oldItem: VehicleStatusEntity,
                    newItem: VehicleStatusEntity
                ): Boolean {
                    return oldItem.speed == newItem.speed
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: VehicleStatusEntity,
                    newItem: VehicleStatusEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

}