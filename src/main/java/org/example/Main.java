package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.Files.list;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Path pathToMusic = Paths.get("C:\\Users\\maxbl\\Music\\Max's Music");
        Path pathToPlaylists = Paths.get("C:\\Users\\maxbl\\Downloads\\playlitss");
        Path pathToSave = Paths.get("C:\\Users\\maxbl\\Music\\Max's Music\\save");
        addSongsToSave(pathToPlaylists, pathToMusic, pathToSave);
    }

    /**
     * Handles m3u only
     *
     * @param p the path
     * @return a list of all the songs in every playlist
     * @throws IOException if there is an error reading the file shouldnt happen tho
     */
    public static List<PlayList> getPlaylists(Path p) throws IOException
    {
        return list(p).filter(j -> (j.getFileName().toString()).endsWith(".m3u")).map(f -> new PlayList(f.toFile())).collect(Collectors.toList());
    }

    public static void addSongsToSave(Path pathToPlaylist, Path pathToMusic, Path output) throws IOException
    {
        recursiveEmpty(pathToMusic);
        File theDir = output.toFile();
        if (!theDir.exists())
        {
            theDir.mkdirs();
        }
        File dupDir = Paths.get(output + "\\" + "duplicates").toFile();
        if (!dupDir.exists())
        {
            dupDir.mkdirs();
        }
        ArrayList<PlayList> playlists = (ArrayList<PlayList>) getPlaylists(pathToPlaylist);
        HashSet<String> toSave = new HashSet<>();
        playlists.forEach(f -> toSave.addAll((f.getSongs())));
        list(pathToMusic).filter(f -> toSave.contains(f.getFileName().toString())).forEach(path ->
                {
                    try
                    {
                        Files.move(path,
                                Paths.get(output + "\\" + path.getFileName()));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        );
        list(pathToMusic).filter(f -> (f.toFile().isDirectory() && f.getFileName().toString().compareTo(
                "save") == 0)).forEach(f ->
                {
                    try
                    {
                        moveFiles(f, dupDir.toPath(), dupDir.toPath());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    public static void moveFiles(Path source, Path destination, Path dump) throws IOException
    {
        list(source).forEach(path ->
        {
            try
            {
                Files.move(path, Paths.get(destination + "\\" + path.getFileName()));
            }
            catch (IOException e)
            {
                try
                {
                    Files.move(path, Paths.get(dump + "\\" + path.getFileName()));
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static void recursiveEmpty(Path path) throws IOException
    {
        list(path).filter(f -> f.toFile().isDirectory()).forEach(f ->
                {
                    try
                    {
                        recursiveEmpty(f);
                        moveFiles(f, f.getParent(), f);
                        Files.delete(f);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
        );
    }
}