
fun FragmentActivity.explain() {
    val dialog = AlertDialog.Builder(this)
    dialog.setMessage("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
        .setPositiveButton("Yes") { paramDialogInterface, _ ->

            paramDialogInterface.dismiss()

            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + this.packageName)
            ).also {
                this.startActivity(it)
            }
        }
        .setNegativeButton(
            getString(R.string.cancel)
        ) { paramDialogInterface, _ -> paramDialogInterface.dismiss() }
    dialog.show()
}
