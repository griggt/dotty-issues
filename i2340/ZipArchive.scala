abstract class ZipArchive {
  sealed class Entry
}

abstract class ManifestResources extends ZipArchive {
  def test = {
    class FileEntry extends Entry
    ???
  }
}
