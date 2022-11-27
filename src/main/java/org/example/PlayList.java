package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayList
{
    List<String> mp3;
    int next;

    public PlayList(File f)
    {
        mp3 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                addMP3(line);
            }
        }
        catch (IOException ex)
        {
            System.out.println("error is reading the file");
        }
        next = -1;
    }

    private void addMP3(String line)
    {
        if (line.endsWith(".mp3"))
            mp3.add(line.substring(line.lastIndexOf('\\') + 1));
    }

    public String getNext()
    {
        next++;
        if (mp3.size() <= next) next = 0;
        return mp3.get(next);
    }

    public List<String> getSongs()
    {
        return mp3;
    }

    @Override
    public String toString()
    {
        return mp3.toString();
    }
}