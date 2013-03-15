package com.karateca.ddescriber.toolWindow;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.karateca.ddescriber.model.JasmineFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andres Dominguez.
 */
public class VirtualFileListener {

  private Map<VirtualFile, CallbackHolder> filesToListenFor = new HashMap<VirtualFile, CallbackHolder>();

  public VirtualFileListener() {
    VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
      @Override
      public void contentsChanged(VirtualFileEvent event) {
        VirtualFile virtualFile = event.getFile();
        if (filesToListenFor.containsKey(virtualFile)) {
          filesToListenFor.get(virtualFile).doCallback();
        }
      }
    });
  }

  public void registerForChangeEvent(JasmineFile jasmineFile, ChangeCallback callback) {
    CallbackHolder callbackHolder = new CallbackHolder(jasmineFile, callback);
    filesToListenFor.put(jasmineFile.getVirtualFile(), callbackHolder);
  }

  class CallbackHolder {
    final JasmineFile jasmineFile;
    final ChangeCallback callback;

    CallbackHolder(JasmineFile jasmineFile, ChangeCallback callback) {
      this.jasmineFile = jasmineFile;
      this.callback = callback;
    }

    void doCallback() {
      callback.contentsChanged(jasmineFile);
    }
  }
}
