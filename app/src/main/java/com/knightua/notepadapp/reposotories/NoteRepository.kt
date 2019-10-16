package com.knightua.notepadapp.reposotories

import android.annotation.SuppressLint
import com.knightua.notepadapp.network.WebServer
import com.knightua.notepadapp.room.dao.NoteDao
import com.knightua.notepadapp.room.entity.Note
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class NoteRepository(private val noteDao: NoteDao, private val webServer: WebServer) {

    fun getAll(): Observable<List<Note>> {
        return Observable.concatArray(
            getAllFromDatabase(),
            getAllFromApi()
        )
    }

    fun getAllFromDatabase(): Observable<List<Note>> {
        return noteDao.getAll()
            .filter {
                it.isNotEmpty()
            }
            .toObservable()
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

    fun getByIdFromDatabase(id: Long): Observable<Note> {
        return noteDao.getById(id)
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
                { Timber.d("Inserted ${notes.size} notes in DataBase") },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun insertInDatabase(note: Note) {
        Single.fromCallable { noteDao.insert(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Insert ${note} in DataBase") },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun updateInDatabase(note: Note) {
        Single.fromCallable { noteDao.update(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Update ${note} in DataBase") },
                { Timber.e(it.toString()) })
    }

    @SuppressLint("CheckResult")
    fun deleteAllFromDatabase(notes: List<Note>) {
        Single.fromCallable { noteDao.deleteAll(notes) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Deleted ${notes.size} from DataBase") },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun deleteFromDatabase(note: Note) {
        Single.fromCallable { noteDao.delete(note) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Delete ${note} from DataBase") },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun deleteFromDatabase(id: Long) {
        Single.fromCallable { noteDao.deleteById(id) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Delete note by ${id} id from DataBase") },
                { Timber.e(it.toString()) }
            )
    }

    @SuppressLint("CheckResult")
    fun clearDatabase() {
        Single.fromCallable { noteDao.clear() }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { Timber.d("Clear all notes from DataBase") },
                { Timber.e(it.toString()) }
            )
    }
}