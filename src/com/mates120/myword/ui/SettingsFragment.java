package com.mates120.myword.ui;

import java.util.ArrayList;
import java.util.List;

import com.mates120.myword.AvailableDictionaries;
import com.mates120.myword.Dictionary;
import com.mates120.myword.R;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.TextView;


public class SettingsFragment extends ListFragment
{	
	private AvailableDictionaries availableDictionaries;
	private List<Dictionary> dicts;
	private DictionaryArrayAdapter mAdapter;
	private Handler uiThreadHandler;
	private MultiChoiceModeListener dictsListListener;
	private List<Integer> dictsToDelete;

	private SelectionSpinner selectionSpinner;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		uiThreadHandler = new Handler();
		mAdapter = new DictionaryArrayAdapter(getActivity(), android.R.id.list);
		availableDictionaries = AvailableDictionaries.getInstance(this.getActivity());
		availableDictionaries.subscribeSettingsFragment(this);
		dictsListListener = createDictsListListener();
		dictsToDelete = new ArrayList<Integer>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings, container, false);	
		selectionSpinner = new SelectionSpinner(getActivity());
		return view;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		new RefreshDictionariesListTask(getActivity());
	};

	public void onDictionariesRefresh()
	{
		dicts = availableDictionaries.getList();
		uiThreadHandler.post(refreshDictsList());
	}

	private Runnable refreshDictsList()
	{
		return new Runnable()
		{
			@Override
			public void run()
			{				
				mAdapter.clear();
				mAdapter.addAll(dicts);
				setListAdapter(mAdapter);
//				getListView().setItemsCanFocus(false);
				installContextMenuForDictsList();
			}
		};
	}

	private MultiChoiceModeListener createDictsListListener()
	{
		return new MultiChoiceModeListener()
		{
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item)
			{
		        switch (item.getItemId())
		        {
	            	case R.id.dictlist_delete:
	            		selectionSpinner.dropSelected();
	            		deleteDictionaries();
	            		dictsToDelete.clear();
	            		mode.finish(); // Action picked, so close the CAB
	            		return true;
	            	default:
	            		return false;
		        }
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.dictlist_actions, menu);
		        return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode)
			{
				selectionSpinner.dropSelected();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu)
			{
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked)
				{
					dictsToDelete.add(position);
					selectionSpinner.increaseSelected();
				} 
				else
				{					
					dictsToDelete.remove(new Integer(position));
					selectionSpinner.decreaseSelected();
				}
				mode.setCustomView(selectionSpinner.getView());
				
			}};
	}
	
	private void deleteDictionaries()
	{
		for (Integer i: dictsToDelete)
			availableDictionaries.deleteDictionary(mAdapter.getItem(i));
	}
	
	private void installContextMenuForDictsList()
	{
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(dictsListListener);
	}
	
	@Override
    public void onListItemClick(ListView l, View view, int position, long id)
	{
		Dictionary dict = mAdapter.getItem(position);
		TextView dictStatusText = (TextView) view.findViewById(R.id.dict_status);
		DictionaryStatus dictStatus = new DictionaryStatus(getActivity(), dictStatusText);		
		if (dict.isActive())
		{
			availableDictionaries.setDictionaryActive(dict, false);
		}
		else
		{
			availableDictionaries.setDictionaryActive(dict, true);
		}
		dictStatus.toggle();
	}
}