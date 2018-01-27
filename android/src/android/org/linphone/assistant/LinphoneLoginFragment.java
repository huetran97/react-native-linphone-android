package org.linphone.assistant;
/*
LinphoneLoginFragment.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import java.util.Locale;

import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneUtils;
import org.linphone.R;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.DialPlan;
import org.linphone.core.LinphoneAccountCreator;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LinphoneLoginFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, OnClickListener, TextWatcher, LinphoneAccountCreator.LinphoneAccountCreatorListener {
	private EditText login, password;
	private Button apply;
	private CheckBox useUsername;
	private LinearLayout  usernameLayout, passwordLayout;
	private TextView forgotPassword, messagePhoneNumber;
	private Boolean recoverAccount;
	private LinphoneAccountCreator accountCreator;
	private int countryCode;
	private String phone, dialcode, username, pwd;
	private ImageView phoneNumberInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.assistant_linphone_login, container, false);

		accountCreator = LinphoneCoreFactory.instance().createAccountCreator(LinphoneManager.getLc(), LinphonePreferences.instance().getXmlrpcUrl());
		accountCreator.setListener(this);
		String url = "http://linphone.org/free-sip-service.html&action=recover";

		login = (EditText) view.findViewById(R.id.assistant_username);
		login.addTextChangedListener(this);

		recoverAccount = true;

		useUsername = (CheckBox) view.findViewById(R.id.use_username);
		usernameLayout = (LinearLayout) view.findViewById(R.id.username_layout);
		passwordLayout = (LinearLayout) view.findViewById(R.id.password_layout);
		password = (EditText) view.findViewById(R.id.assistant_password);
		messagePhoneNumber = (TextView) view.findViewById(R.id.message_phone_number);

		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);

		apply = (Button) view.findViewById(R.id.assistant_apply);
		apply.setEnabled(true);
		apply.setOnClickListener(this);

		if(getResources().getBoolean(R.bool.assistant_allow_username)) {
			useUsername.setVisibility(View.VISIBLE);
			useUsername.setOnCheckedChangeListener(this);
			password.addTextChangedListener(this);
			forgotPassword.setText(Compatibility.fromHtml("<a href=\"" + url + "\"'>" + getString(R.string.forgot_password) + "</a>"));
			forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
		}

		//Hide phone number and display username/email/password
		if(!getResources().getBoolean(R.bool.use_phone_number_validation)){
//			phoneNumberLayout.setVisibility(View.GONE);
			useUsername.setVisibility(View.GONE);

			usernameLayout.setVisibility(View.VISIBLE);
			passwordLayout.setVisibility(View.VISIBLE);
		}

		// When we come from generic login fragment
		username = getArguments().getString("Username");
		pwd = getArguments().getString("Password");
		if (username != null && pwd != null) {
			useUsername.setChecked(true);
			onCheckedChanged(useUsername, true);
			login.setText(username);
			password.setText(pwd);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			accountCreator.setLanguage(Locale.getDefault().toLanguageTag());
		}

//		addPhoneNumberHandler(dialCode, null);
//		addPhoneNumberHandler(phoneNumberEdit, null);

		return view;
	}

	public void linphoneLogIn() {
		if (login.getText() == null || login.length() == 0 || password.getText() == null || password.length() == 0) {
			LinphoneUtils.displayErrorAlert(getString(R.string.first_launch_no_login_password), AssistantActivity.instance());
			apply.setEnabled(true);
			return;
		}
		accountCreator.setUsername(login.getText().toString());
		accountCreator.setPassword(password.getText().toString());
		accountCreator.isAccountUsed();
	}


	@Override
	public void onResume() {
		super.onResume();
		if (useUsername != null && useUsername.isChecked())
			recoverAccount = false;
		else
			recoverAccount = true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.assistant_apply){
			apply.setEnabled(false);

				linphoneLogIn();

		}


	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.getId() == R.id.use_username) {
			if(isChecked) {
				usernameLayout.setVisibility(View.VISIBLE);
				passwordLayout.setVisibility(View.VISIBLE);
				messagePhoneNumber.setText(getString(R.string.assistant_linphone_login_desc));
				recoverAccount = false;
			}
		}
	}

	@Override
	public void onAccountCreatorIsAccountUsed(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus status) {
		if (AssistantActivity.instance() == null) {
			apply.setEnabled(true);
			return;
		}
		if (status.equals(LinphoneAccountCreator.RequestStatus.AccountExist) || status.equals(LinphoneAccountCreator.RequestStatus.AccountExistWithAlias)) {
			AssistantActivity.instance().linphoneLogIn(accountCreator);
		} else {
			LinphoneUtils.displayErrorAlert(LinphoneUtils.errorForRequestStatus(status), AssistantActivity.instance());
		}
		apply.setEnabled(true);
	}

	@Override
	public void onAccountCreatorAccountCreated(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus status) {
	}

	@Override
	public void onAccountCreatorAccountActivated(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus status) {
	}

	@Override
	public void onAccountCreatorAccountLinkedWithPhoneNumber(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus RequestStatus) {

	}

	@Override
	public void onAccountCreatorPhoneNumberLinkActivated(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus RequestStatus) {

	}

	@Override
	public void onAccountCreatorIsAccountActivated(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus status) {
	}

	@Override
	public void onAccountCreatorPhoneAccountRecovered(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus RequestStatus) {

	}

	@Override
	public void onAccountCreatorIsAccountLinked(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus RequestStatus) {

	}

	@Override
	public void onAccountCreatorIsPhoneNumberUsed(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus RequestStatus) {

	}

	@Override
	public void onAccountCreatorPasswordUpdated(LinphoneAccountCreator accountCreator, LinphoneAccountCreator.RequestStatus status) {

	}
}
