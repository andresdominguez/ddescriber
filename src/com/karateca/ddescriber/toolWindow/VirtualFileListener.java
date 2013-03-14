package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andres Dominguez.
 */
public class VirtualFileListener {

  Map<VirtualFile, ChangeCallback> filesToListenFor = new HashMap<VirtualFile, ChangeCallback>();

  public VirtualFileListener() {
    VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
      @Override
      public void contentsChanged(VirtualFileEvent event) {
        System.out.println("contents changed " + event);
        VirtualFile virtualFile = event.getFile();
        if (filesToListenFor.containsKey(virtualFile)) {
          filesToListenFor.get(virtualFile).contentsChanged();
        }
      }
    });
  }

  public void registerForChangeEvent(VirtualFile virtualFile, ChangeCallback callback) {
    filesToListenFor.put(virtualFile, callback);
  }
}
