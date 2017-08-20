package com.example.zpfr3739.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by ZPFR3739 on 10/08/2017.
 */

public class ListActivity extends Fragment{

    DBHandler myDbHandler;
    ListView mlistView;
    SimpleCursorAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

           /* case R.id.app_bar_search:
                ((MainActivity) getActivity()).toastMessage("Vous avez cliqué sur search");
                //
                return true;*/

            case R.id.app_bar_refresh:
                ((MainActivity) getActivity()).toastMessage("Vous avez cliqué sur refresh");
                populateListView();
                //
                return true;

        }
        return false;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("List");

        mlistView = (ListView) getView().findViewById(R.id.liste_note_view);
        myDbHandler = new DBHandler(getActivity());

        //insérer les notes dans une listview et les afficher
        populateListView();

        //faire une action lorsqu'on clique longtemps sur une note
        mlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

                //
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setTitle("Attention!!");
                alert.setMessage("Are you sure to delete this record");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //action si confirmation de la suppression
                        myDbHandler.removeNote(id);
                        populateListView();
                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();

                return true;

            }

        });

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {

                //action quand on fait un click sur un element de la liste
                //passage vers le fragment

                Fragment newWriteFrag = new WriteActivity();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, newWriteFrag);
                ft.commit();

                //renvoyer l'ID, le titre et le contenu de la note

            }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.list_menu, menu);

        MenuItem itemSearch = menu.findItem(R.id.app_bar_search);
        SearchView searchView = new SearchView(getActivity());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        itemSearch.setActionView(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void populateListView(){

        //recuperer les données et les insérer dans la liste
        Cursor data = myDbHandler.getDataNote();


        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2,
                data,
                new String[] { DBHandler.NOTE_TITRE, DBHandler.NOTE_CONTENT},
                new int[] { android.R.id.text1, android.R.id.text2 });

        mlistView.setAdapter(adapter);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = constraint.toString();
                return myDbHandler.getNoteByTitle(partialValue);
            }
    });

    }

}
