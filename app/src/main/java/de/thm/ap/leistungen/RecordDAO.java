package de.thm.ap.leistungen;

import android.content.Context;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

public class RecordDAO {
    private static String FILE_NAME = "records.obj";
    private Context ctx;
    private List<Record> records;
    private int nextId = 1;
    public RecordDAO(Context ctx) {
        this.ctx = ctx;
        initRecords();
    }
    public List<Record> findAll() {
        return records;
    }
    public Optional<Record> findById(int id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == id) {
                return Optional.ofNullable(records.get(i));
            }
        }
        return null;
    }
    /**
     * Ersetzt das übergebene {@link Record} Objekt mit einem bereits
     gespeicherten {@link Record} Objekt mit gleicher id.
     *
     * @param record
     * @return true = update ok, false = kein {@link Record} Objekt mit gleicher
    id im Speicher gefunden
     */
    public boolean update(Record record) {
        Integer id = record.getId();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(id)) {
                Record r = records.get(i);
                r.setModuleNum(record.getModuleNum());
                r.setModuleName(record.getModuleName());
                r.setSummerTerm(record.isSummerTerm());
                r.setHalfWeighted(record.isHalfWeighted());
                r.setYear(record.getYear());
                r.setCrp(record.getCrp());
                r.setMark(record.getMark());
                saveRecords();
                return true;
            }
        } return false;
    }

    public boolean delete(Record record) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId().equals(record.getId())) {
                records.remove(i);
                saveRecords();
                return true;
            }
        }
        return false;
    }
    /**
     * Persistiert das übergebene {@link Record} Objekt und liefert die neue id
     zurück.
     *
     * @param record
     * @return neue record id
     */
    public int persist(Record record) {
        record.setId(nextId);
        records.add(record);
        nextId++;
        saveRecords();
        return record.getId();
    }

    @SuppressWarnings("unchecked")
    private void initRecords() {
        File f = ctx.getFileStreamPath(FILE_NAME);
        if (f.exists()) {
            try (FileInputStream in = ctx.openFileInput(FILE_NAME)) {
                Object obj = obj = new ObjectInputStream(in).readObject();
                records = (List<Record>) obj;
                // init next id
                records.stream()
                        .mapToInt(Record::getId)
                        .max()
                        .ifPresent(id -> nextId = id + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            records = new ArrayList<>();
        }
    }
    private void saveRecords() {
        try (FileOutputStream out = ctx.openFileOutput(FILE_NAME, Context.
                MODE_PRIVATE)) {
            new ObjectOutputStream(out).writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
