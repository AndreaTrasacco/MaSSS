// Generated by view binder compiler. Do not edit!
package it.unipi.masss.project.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.switchmaterial.SwitchMaterial;
import it.unipi.masss.project.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentSettingsBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final SwitchMaterial autoMonSw;

  @NonNull
  public final CheckBox chosenContactOpt;

  @NonNull
  public final CheckBox closeContactOpt;

  @NonNull
  public final TextView textView;

  @NonNull
  public final TextView textView2;

  private FragmentSettingsBinding(@NonNull ConstraintLayout rootView,
      @NonNull SwitchMaterial autoMonSw, @NonNull CheckBox chosenContactOpt,
      @NonNull CheckBox closeContactOpt, @NonNull TextView textView, @NonNull TextView textView2) {
    this.rootView = rootView;
    this.autoMonSw = autoMonSw;
    this.chosenContactOpt = chosenContactOpt;
    this.closeContactOpt = closeContactOpt;
    this.textView = textView;
    this.textView2 = textView2;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentSettingsBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentSettingsBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_settings, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentSettingsBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.auto_mon_sw;
      SwitchMaterial autoMonSw = ViewBindings.findChildViewById(rootView, id);
      if (autoMonSw == null) {
        break missingId;
      }

      id = R.id.chosen_contact_opt;
      CheckBox chosenContactOpt = ViewBindings.findChildViewById(rootView, id);
      if (chosenContactOpt == null) {
        break missingId;
      }

      id = R.id.close_contact_opt;
      CheckBox closeContactOpt = ViewBindings.findChildViewById(rootView, id);
      if (closeContactOpt == null) {
        break missingId;
      }

      id = R.id.textView;
      TextView textView = ViewBindings.findChildViewById(rootView, id);
      if (textView == null) {
        break missingId;
      }

      id = R.id.textView2;
      TextView textView2 = ViewBindings.findChildViewById(rootView, id);
      if (textView2 == null) {
        break missingId;
      }

      return new FragmentSettingsBinding((ConstraintLayout) rootView, autoMonSw, chosenContactOpt,
          closeContactOpt, textView, textView2);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}