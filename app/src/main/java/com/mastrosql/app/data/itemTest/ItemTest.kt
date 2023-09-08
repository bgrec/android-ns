package com.mastrosql.app.data.itemTest

data class ItemTest(
    val id: Int,
    val ean: String? = null,
    val description: String? = null,
    val quantity: Double? = null,
    //val price: Double? = null,
)

/*data class Affirmation(
    val stringResourceId: Int,
    val imageResourceId: Int
)
Annotate the stringResourceId property with the @StringRes annotation and annotate the imageResourceId with the @DrawableRes annotation. The stringResourceId represents an ID for the affirmation text stored in a string resource. The imageResourceId represents an ID for the affirmation image stored in a drawable resource.
Affirmation.kt


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Affirmation(
    @StringRes val stringResourceId: Int,
    @DrawableRes val imageResourceId: Int
)
In the com.example.affirmations.data package, open the Datasource.kt file and uncomment the two import statements and the contents of the Datasource class.
Datasource.kt


import com.example.affirmations.R
import com.example.affirmations.model.Affirmation

class Datasource() {
    fun loadAffirmations(): List<Affirmation> {
        return listOf<Affirmation>(
            Affirmation(R.string.affirmation1, R.drawable.image1),
            Affirmation(R.string.affirmation2, R.drawable.image2),
            Affirmation(R.string.affirmation3, R.drawable.image3),
            Affirmation(R.string.affirmation4, R.drawable.image4),
            Affirmation(R.string.affirmation5, R.drawable.image5),
            Affirmation(R.string.affirmation6, R.drawable.image6),
            Affirmation(R.string.affirmation7, R.drawable.image7),
            Affirmation(R.string.affirmation8, R.drawable.image8),
            Affirmation(R.string.affirmation9, R.drawable.image9),
            Affirmation(R.string.affirmation10, R.drawable.image10))
    }
}
*/