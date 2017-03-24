package com.mpsdevelopment.helicopter.license;

import com.mpsdevelopment.helicopter.license.dialogfx.DialogFX;

public class LicenseDialogUtils {

	public static void showLicenseException() {

		DialogFX afterDialog = new DialogFX(DialogFX.Type.ERROR);
		afterDialog.setTitleText("Error");
		afterDialog.setMessage("Program abruptly has stopped working. Contact an administrator");
		afterDialog.showDialog();
	}
}
