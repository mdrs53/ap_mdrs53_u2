package de.thm.ap.leistungen;



import java.util.List;
import de.thm.ap.leistungen.model.Record;


class Stats {
    private List<Record> records;
    int sumCrp = 0;
    int crpToEnd = 180;
    int sumHalfWeighted = 0;
    int averageMark = 0;

    public Stats(List<Record> records) {
        this.records = records;
        setSumCrp();
        setCrpToEnd();
        setSumHalfWeighted();
        setAverageMark();
    }

    public int getSumCrp() {
        return sumCrp;
    }

    private void setSumCrp() {
        for (int i = 0; i < records.size(); i++) {
            sumCrp += records.get(i).getCrp();
        }
    }

    public int getCrpToEnd() {
        return crpToEnd;
    }

    private void setCrpToEnd() {
        crpToEnd -= sumCrp;
    }

    public int getSumHalfWeighted() {
        return sumHalfWeighted;
    }

    private void setSumHalfWeighted() {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isHalfWeighted()) {
                sumHalfWeighted++;
            }
        }
    }

    public int getAverageMark() {
        return averageMark;
    }

    private void setAverageMark() {
        int sum = 0;
        int newSumCrp = 0;
        int newCrp = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getMark() == 0) {
                continue;
            }
            if (records.get(i).isHalfWeighted()) {
                newCrp = records.get(i).getCrp() / 2;
                newSumCrp += newCrp;
            } else {
                newCrp = records.get(i).getCrp();
                newSumCrp += newCrp;
            }
            sum = sum + (newCrp * records.get(i).getMark());
        }
        averageMark = sum / newSumCrp;
    }
}
