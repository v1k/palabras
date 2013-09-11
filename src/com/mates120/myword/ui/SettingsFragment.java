package com.mates120.myword.ui;

import java.util.List;

import com.mates120.myword.Dictionary;
import com.mates120.myword.DictionaryManager;
import com.mates120.myword.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.TextView;


public class SettingsFragment extends ListFragment implements
		AbsListView.OnItemClickListener {
	
	private DictionaryManager dictionaryManager;
	private List<Dictionary> dicts;

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	public static final String ARG_SECTION_NUMBER = "param1";

	// TODO: Rename and change types of parameters
	private String mParam1;

	private OnSettingsFragmentInteractionListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private DictionaryArrayAdapter mAdapter;

	// TODO: Rename and change types of parameters
	public static SettingsFragment newInstance(String param1, String param2) {
		SettingsFragment fragment = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_SECTION_NUMBER, param1);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_SECTION_NUMBER);
		}
		
		dictionaryManager = new DictionaryManager(this.getActivity());
		dicts = dictionaryManager.getDictionaries();
		mAdapter = new DictionaryArrayAdapter(getActivity(),
				android.R.id.list, dicts, dictionaryManager);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container,
				false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		checkDicts();
		mListView.setOnItemClickListener(this);

		return view;
	}
	
	private void checkDicts(){
		int i = 0;
		for (Dictionary item : dicts) {
		    // Check a field/condition in the object
		    if (item.isSearchIn()) {
		        mListView.setItemChecked(i,true);
		    }
		    i++;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSettingsFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i("ARG2: ", String.valueOf(position));
		Log.i("ARG3: ", String.valueOf(id));
		CheckedTextView textView = (CheckedTextView) view;
		if (textView.isChecked()){
			dictionaryManager.setSearchInDict(
					textView.getText().toString(), false);
			Log.i("SET DICTIONARY", "SET FALSE");
		}else{
			dictionaryManager.setSearchInDict(
					textView.getText().toString(), true);
			Log.i("SET DICTIONARY", "SET TRUE");
		}
		Log.i("SET DICTIONARY", "SET TRUE");
		textView.setChecked(!textView.isChecked());
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnSettingsFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

}