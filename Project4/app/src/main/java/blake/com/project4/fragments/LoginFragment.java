package blake.com.project4.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import blake.com.project4.R;

/**
 * Created by Raiders on 5/7/16.
 */
public class LoginFragment extends Fragment {

    Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        button = (Button) view.findViewById(R.id.remove_fragment_button);
        return view;
    }

    private void setButton() {

    }
}
