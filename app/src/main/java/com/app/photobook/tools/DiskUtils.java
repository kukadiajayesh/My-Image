package com.app.photobook.tools;

import android.os.Environment;
import android.os.StatFs;

/**
 * Created by Tom on 7/15/13.
 * Some helper methods for FS queries.
 */
public class DiskUtils {
    private static final long MEGA_BYTE = 1048576;

    /**
     * Calculates total space on disk
     *
     * @param external If true will query external disk, otherwise will query internal disk.
     * @return Number of mega bytes on disk.
     */
    public static int totalSpace(boolean external) {
        StatFs statFs = getStats(external);
        long total = (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())) / MEGA_BYTE;
        return (int) total;
    }

    /**
     * Calculates free space on disk
     *
     * @param external If true will query external disk, otherwise will query internal disk.
     * @return Number of free mega bytes on disk.
     */
    public static int freeSpace(boolean external) {
        StatFs statFs = getStats(external);
        long availableBlocks = statFs.getAvailableBlocks();
        long blockSize = statFs.getBlockSize();
        long freeBytes = availableBlocks * blockSize;

        return (int) (freeBytes / MEGA_BYTE);
    }

    /**
     * Calculates occupied space on disk
     *
     * @param external If true will query external disk, otherwise will query internal disk.
     * @return Number of occupied mega bytes on disk.
     */
    public static int busySpace(boolean external) {
        StatFs statFs = getStats(external);
        long total = (statFs.getBlockCount() * statFs.getBlockSize());
        long free = (statFs.getAvailableBlocks() * statFs.getBlockSize());

        return (int) ((total - free) / MEGA_BYTE);
    }

    private static StatFs getStats(boolean external) {
        String path;

        if (external) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = Environment.getRootDirectory().getAbsolutePath();
        }

        return new StatFs(path);
    }

}
