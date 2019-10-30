package com.knightua.notepadapp.reposotories

import android.annotation.SuppressLint
import com.jakewharton.rxrelay2.BehaviorRelay
import com.knightua.notepadapp.network.WebServer
import com.knightua.notepadapp.room.dao.NoteDao
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class NoteRepository(private val noteDao: NoteDao, private val webServer: WebServer) {

    companion object {
        const val DATA_EMPTY = 0
        const val DATA_INSERTED = 1
        const val DATA_UPDATED = 2
        const val DATA_DELETED = 3
        const val DATA_CLEARED = 4
    }

    private val databaseStateRelay =
        BehaviorRelay.createDefault(Pair(DATA_EMPTY, emptyList<Note>()))

    fun getAll(): Observable<List<Note>> {
        return Observable.concatArray(
            getAllFromApi()
        )
    }

    fun getAllFromDatabase(): Flowable<List<Note>> {
        return noteDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d("Dispatching ${it.size} notes from DataBase...")
            }
    }

    fun getAllFromApi(): Observable<List<Note>> {
        return webServer.getAllNotes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d("Dispatching ${it.size} notes from Api...")
                insertAllInDatabase(it)
            }
    }

    fun getByIdFromDatabase(uuid: String): Observable<Note> {
        return noteDao.getById(uuid)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d("Dispatching ${it} notes from DataBase...")
            }
    }

    fun getCount(): Observable<Int> {
        return noteDao.getCount()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Timber.d("Count of notes is %d", it)
            }
    }

    @SuppressLint("CheckResult")
    fun insertAllInDatabase(notes: List<Note>) {
        Single.fromCallable { noteDao.insertAll(notes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_INSERTED, notes))
                    Timber.d("Inserted ${notes.size} notes in DataBase")
                },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun insertInDatabase(note: Note) {
        Single.fromCallable { noteDao.insert(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_INSERTED, listOf(note)))
                    Timber.d("Insert ${note} in DataBase")
                },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun updateInDatabase(note: Note) {
        Single.fromCallable { noteDao.update(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_UPDATED, listOf(note)))
                    Timber.d("Update ${note} in DataBase")
                },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun deleteAllFromDatabase(notes: List<Note>) {
        Single.fromCallable { noteDao.deleteAll(notes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_DELETED, notes))
                    Timber.d("Deleted ${notes.size} from DataBase")
                },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun deleteFromDatabase(note: Note) {
        Single.fromCallable { noteDao.delete(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_DELETED, listOf(note)))
                    Timber.d("Delete ${note} from DataBase")
                },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun deleteFromDatabase(uuid: String) {
        Single.fromCallable { noteDao.deleteById(uuid) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_DELETED, listOf()))
                    Timber.d("Delete note by ${uuid} id from DataBase")
                },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun clearDatabase() {
        Single.fromCallable { noteDao.clear() }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    databaseStateRelay.accept(Pair(DATA_CLEARED, listOf()))
                    Timber.d("Clear all notes from DataBase")
                },
                { Timber.e(it.toString()) }
            )
    }

    fun getObserverForDatabase(): Observable<Pair<Int, List<Note>>> {
        return databaseStateRelay.observeOn(AndroidSchedulers.mainThread())
    }
}