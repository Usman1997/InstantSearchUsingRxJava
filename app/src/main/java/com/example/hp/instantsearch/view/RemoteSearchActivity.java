package com.example.hp.instantsearch.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;

import com.example.hp.instantsearch.R;
import com.example.hp.instantsearch.adapter.ContactAdapter;
import com.example.hp.instantsearch.model.Contact;
import com.example.hp.instantsearch.network.ApiClient;
import com.example.hp.instantsearch.network.ApiService;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RemoteSearchActivity extends AppCompatActivity implements ContactAdapter.ContactsAdapterListener {

    private static final String TAG = RemoteSearchActivity.class.getSimpleName();
    private ApiService apiService;
    private List<Contact> contactList = new ArrayList<>();
    private ContactAdapter mAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();
    private PublishSubject<String> publishSubject = PublishSubject.create();


    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_search);
        unbinder = ButterKnife.bind(this);

        mAdapter = new ContactAdapter(this,contactList,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        apiService = ApiClient.getClient().create(ApiService.class);

        DisposableObserver<List<Contact>> observer = getSearchObserver();

        disposable.add(
                publishSubject
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .switchMapSingle(new Function<String, Single<List<Contact>>>() {
                            @Override
                            public Single<List<Contact>> apply(String s) throws Exception {
                                return apiService.getContacts(null, s)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());
                            }
                        })
                        .subscribeWith(observer));


        // skipInitialValue() - skip for the first time when EditText empty
        disposable.add(
                RxTextView.textChangeEvents(inputSearch)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchContactTextWatcher()));

        disposable.add(observer);

        // passing empty string fetches all the contacts
        publishSubject.onNext("");


    }

    private DisposableObserver<List<Contact>> getSearchObserver(){
        return new DisposableObserver<List<Contact>>() {
            @Override
            public void onNext(List<Contact> contacts) {
                contactList.clear();
                contactList.addAll(contacts);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
                }

            @Override
            public void onComplete() {

            }
        };
    }


    private DisposableObserver<TextViewTextChangeEvent> searchContactTextWatcher(){
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                publishSubject.onNext(textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());

            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
        unbinder.unbind();

    }

    @Override
    public void onContactSelected(Contact contact) {

    }
}
