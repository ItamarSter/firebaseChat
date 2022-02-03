package itamar.stern.arabic.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.R
import itamar.stern.arabic.databinding.MessageItemBinding
import itamar.stern.arabic.models.Message
import itamar.stern.arabic.models.RoomMessage
import itamar.stern.arabic.utils.dp

class MessagesAdapter(private val messages: List<RoomMessage>): RecyclerView.Adapter<MessagesAdapter.VH>() {
    class VH(val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            MessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        //because recyclerView is recycling the layouts which it already used,
        //we need to set all the layout settings again before all use, otherwise -
        //the previous settings like color, width and margin will remain active.
        with(holder.binding){
            with(messages[position]){
                textViewTime.text = time
                textViewMessage.text = message
                //Show the date layout just before first message on chat, and before all new day. (00:00):
                if (position == 0){
                    textViewDate.visibility = View.VISIBLE
                    textViewDate.text = date
                } else {
                    val day = Integer.parseInt(date!!.split("/")[0])
                    val month = Integer.parseInt(date.split("/")[1])
                    val year = Integer.parseInt(date.split("/")[2])
                    val prevDay = Integer.parseInt(messages[position-1].date!!.split("/")[0])
                    val prevMonth = Integer.parseInt(messages[position-1].date!!.split("/")[1])
                    val prevYear = Integer.parseInt(messages[position-1].date!!.split("/")[2])
                    if (year > prevYear || month > prevMonth || day > prevDay){
                        textViewDate.visibility = View.VISIBLE
                        textViewDate.text = date
                    } else {
                        textViewDate.visibility = View.GONE
                    }
                }
                //if the same user wrote message after message - remove the space between the
                //messages and write his name just on the first message:
                if (position > 0 && senderId == messages[position - 1].senderId) {
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, -2)
                    params.setMargins(16.dp().toInt(), 0, 16.dp().toInt(), 1.dp().toInt())
                    layoutOfCards.layoutParams = params
                } else {
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, -2)
                    params.setMargins(16.dp().toInt(), 8.dp().toInt(), 16.dp().toInt(), 1.dp().toInt())
                    layoutOfCards.layoutParams = params
                }


                //customize the currentUser's messages:
                if (FirebaseAuth.getInstance().currentUser?.uid == senderId) {
                    card.background.setTint(root.context.getColor(R.color.greenMyMessages))
                    textViewMessage.setTextColor(Color.WHITE)
                    textViewTime.setTextColor(Color.WHITE)
                    layoutOfTexts.gravity = Gravity.START
                    layoutOfCards.gravity = Gravity.START
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(40.dp().toInt(), 0,0,0)
                    card.layoutParams = params
                } else {
                    card.background.setTint(root.context.getColor(R.color.grayElsesMessages))
                    textViewMessage.setTextColor(Color.BLACK)
                    textViewTime.setTextColor(Color.BLACK)
                    layoutOfTexts.gravity = Gravity.END
                    layoutOfCards.gravity = Gravity.END
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(0, 0,40.dp().toInt(),0)
                    card.layoutParams = params
                    //Color unread messages:
                    if(!wasRead){
                        layoutOfTexts.setBackgroundColor(Color.YELLOW)
                    }
                }
            }
        }
    }

    override fun getItemCount() = messages.size

}