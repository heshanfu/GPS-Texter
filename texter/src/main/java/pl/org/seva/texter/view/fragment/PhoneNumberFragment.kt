/*
 * Copyright (C) 2017 Wiktor Nizio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.org.seva.texter.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast

import java.util.ArrayList

import pl.org.seva.texter.R

class PhoneNumberFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var toast: Toast? = null

    private var contactsEnabled: Boolean = false
    private lateinit var contactKey: String
    private var contactName: String? = null

    private var adapter: SimpleCursorAdapter? = null
    private var number: EditText? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_number, container, false)

        number = view.findViewById(R.id.number) as EditText

        contactsEnabled = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

        val contacts = view.findViewById(R.id.contacts) as ListView
        if (!contactsEnabled) {
            contacts.visibility = View.GONE
        } else {
            contacts.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ -> this.onItemClick(parent, position) }
            adapter = SimpleCursorAdapter(
                    activity,
                    R.layout.item_contact, null,
                    FROM_COLUMNS,
                    TO_IDS,
                    0)
            contacts.adapter = adapter
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loaderManager.initLoader(CONTACTS_QUERY_ID, null, this)
    }

    override fun toString(): String {
        return number!!.text.toString()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        if (!contactsEnabled) {
            return null
        }
        when (id) {
            CONTACTS_QUERY_ID -> {
                val contactsSelectionArgs = arrayOf("1")
                return CursorLoader(
                        activity,
                        ContactsContract.Contacts.CONTENT_URI,
                        CONTACTS_PROJECTION,
                        CONTACTS_SELECTION,
                        contactsSelectionArgs,
                        CONTACTS_SORT)
            }
            DETAILS_QUERY_ID -> {
                val detailsSelectionArgs = arrayOf(contactKey)
                return CursorLoader(
                        activity,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        DETAILS_PROJECTION,
                        DETAILS_SELECTION,
                        detailsSelectionArgs,
                        DETAILS_SORT)
            }
            else -> return null
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        when (loader.id) {
            CONTACTS_QUERY_ID -> adapter!!.swapCursor(data)
            DETAILS_QUERY_ID -> {
                val numbers = ArrayList<String>()
                while (data.moveToNext()) {
                    val n = data.getString(DETAILS_NUMBER_INDEX)
                    if (!numbers.contains(n)) {
                        numbers.add(n)
                    }
                }
                data.close()
                if (numbers.size == 1) {
                    this.number!!.setText(numbers[0])
                } else if (numbers.isEmpty()) {
                    toast = Toast.makeText(
                            context,
                            R.string.no_number,
                            Toast.LENGTH_SHORT)
                    toast!!.show()
                } else {
                    val items = numbers.toTypedArray()
                    AlertDialog.Builder(activity).setItems(items) { dialog, which ->
                        dialog.dismiss()
                        number!!.setText(numbers[which])
                    }.setTitle(contactName).setCancelable(true).setNegativeButton(
                            android.R.string.cancel
                    ) { dialog, _ -> dialog.dismiss() }.show()
                }
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            CONTACTS_QUERY_ID -> adapter!!.swapCursor(null)
            DETAILS_QUERY_ID -> {
            }
        }
    }

    private fun onItemClick(parent: AdapterView<*>, position: Int) {
        toast?.cancel()
        val cursor = (parent.adapter as SimpleCursorAdapter).cursor
        cursor.moveToPosition(position)
        contactKey = cursor.getString(CONTACT_KEY_INDEX)
        contactName = cursor.getString(CONTACT_NAME_INDEX)

        loaderManager.restartLoader(DETAILS_QUERY_ID, null, this)
    }

    fun setNumber(number: String?) {
        this.number!!.setText(number)
    }

    companion object {

        private val CONTACTS_QUERY_ID = 0
        private val DETAILS_QUERY_ID = 1

        private val FROM_COLUMNS = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

        private val CONTACTS_PROJECTION = arrayOf( // SELECT
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.HAS_PHONE_NUMBER)

        private val CONTACTS_SELECTION = // FROM
                ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?"

        private val CONTACTS_SORT = // ORDER_BY
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY

        private val DETAILS_PROJECTION = arrayOf( // SELECT
                ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.LABEL)

        private val DETAILS_SORT = // ORDER_BY
                ContactsContract.CommonDataKinds.Phone._ID

        private val DETAILS_SELECTION = // WHERE
                ContactsContract.Data.LOOKUP_KEY + " = ?"


        // The column index for the LOOKUP_KEY column
        private val CONTACT_KEY_INDEX = 1
        private val CONTACT_NAME_INDEX = 2
        private val DETAILS_NUMBER_INDEX = 1

        private val TO_IDS = intArrayOf(android.R.id.text1)
    }
}