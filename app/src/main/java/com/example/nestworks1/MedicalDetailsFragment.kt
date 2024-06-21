package com.example.nestworks1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MedicalDetailsFragment : Fragment() {

    private lateinit var editTextFamilyMember: EditText
    private lateinit var editTextMorningMedication: EditText
    private lateinit var editTextEveningMedication: EditText
    private lateinit var calendarViewAppointmentDate: CalendarView
    private lateinit var textViewAppointmentDate: TextView
    private lateinit var buttonSave: Button
    private lateinit var buttonRetrieve: Button

    private val database = FirebaseDatabase.getInstance().reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medical_details, container, false)
        initializeViews(view)
        return view
    }

    private fun initializeViews(view: View) {
        editTextFamilyMember = view.findViewById(R.id.editTextFamilyMember)
        editTextMorningMedication = view.findViewById(R.id.editTextMorningMedication)
        editTextEveningMedication = view.findViewById(R.id.editTextEveningMedication)
        calendarViewAppointmentDate = view.findViewById(R.id.calendarViewAppointmentDate)
        textViewAppointmentDate = view.findViewById(R.id.textViewAppointmentDate)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonRetrieve = view.findViewById(R.id.buttonRetrieve)

        calendarViewAppointmentDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
            textViewAppointmentDate.text = "Selected Appointment Date: $selectedDate"
        }

        buttonSave.setOnClickListener {
            saveMedicalDetails()
        }

        buttonRetrieve.setOnClickListener {
            retrieveAndShowMedicalDetails()
        }
    }

    private fun saveMedicalDetails() {
        val familyMemberName = editTextFamilyMember.text.toString().trim()
        val morningMedication = editTextMorningMedication.text.toString().trim()
        val eveningMedication = editTextEveningMedication.text.toString().trim()

        if (familyMemberName.isNotEmpty() && morningMedication.isNotEmpty() && selectedDate.isNotEmpty()) {
            val familyMember = FamilyMember(
                name = familyMemberName,
                morningMedication = morningMedication,
                eveningMedication = eveningMedication,
                appointmentDate = selectedDate
            )

            val currentUserUid = currentUser?.uid

            currentUserUid?.let { uid ->
                val familyMembersRef = database.child("users").child(uid).child("familyMembers")
                val memberKey = familyMembersRef.push().key ?: ""

                familyMembersRef.child(memberKey).setValue(familyMember)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Medical details saved successfully", Toast.LENGTH_SHORT).show()
                        clearFields()
                    }
                    .addOnFailureListener { e ->
                        Log.e("MedicalDetailsFragment", "Error saving medical details", e)
                        Toast.makeText(requireContext(), "Failed to save medical details", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        editTextFamilyMember.text.clear()
        editTextMorningMedication.text.clear()
        editTextEveningMedication.text.clear()
        textViewAppointmentDate.text = "Selected Appointment Date: "
        selectedDate = ""
    }

    private fun retrieveAndShowMedicalDetails() {
        val currentUserUid = currentUser?.uid

        currentUserUid?.let { uid ->
            val familyMembersRef = database.child("users").child(uid).child("familyMembers")

            familyMembersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val builder = StringBuilder()

                        for (familyMemberSnapshot in snapshot.children) {
                            val familyMember = familyMemberSnapshot.getValue(FamilyMember::class.java)
                            familyMember?.let {
                                builder.append("Name: ${it.name}\n")
                                builder.append("Morning Medication: ${it.morningMedication}\n")
                                builder.append("Evening Medication: ${it.eveningMedication}\n")
                                builder.append("Appointment Date: ${it.appointmentDate}\n\n")
                            }
                        }

                        showMedicalDetailsDialog(builder.toString())
                    } else {
                        Toast.makeText(requireContext(), "No medical details found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MedicalDetailsFragment", "Error retrieving medical details", error.toException())
                    Toast.makeText(requireContext(), "Failed to retrieve medical details", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showMedicalDetailsDialog(details: String) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_medical_details, null)
        builder.setView(dialogView)

        val textViewDetails = dialogView.findViewById<TextView>(R.id.textViewDetails)
        val buttonClose = dialogView.findViewById<Button>(R.id.buttonClose)

        textViewDetails.text = details

        val dialog = builder.create()

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
