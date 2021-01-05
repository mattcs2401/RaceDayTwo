package com.mcssoft.racedaytwo.interfaces

interface IDeleteAll {
    /**
     * An interface between the MainFragment and the DeleteAllDialog. The MainFragment implements
     * the interface and a reference is passed in the dialog's constructor.
     */
    /**
     * Get the delete all flag.
     * @param deleteAll: True - delete all from recycler view listing, else - false (do nothing).
     */
    fun deleteAll(deleteAll: Boolean)
}