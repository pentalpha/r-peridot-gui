repositories.remote << 'http://www.ibiblio.org/maven2/'
repositories.remote << 'http://repo1.maven.org/maven2'

VERSION_NUMBER = '1.0'
TARGET_JVM = '1.8'

desc 'GUI for differential expression analysis on gene count reads data'
define 'r-peridot-gui' do
  project.version = VERSION_NUMBER
  project.group = 'BioME'
  compile.with Dir[_("lib/*.jar")]
  compile.options.target = TARGET_JVM
  compile.into _('out')

  resources.from _('src/main/resource/')

  package :file=>_("jar/r-peridot-gui.jar")
  
  package(:jar).with(:manifest=>_("src/main/META-INF/MANIFEST.MF")).exclude('.scala-deps').merge(compile.dependencies)

  task :make => :package do
    system './downloadScripts.sh'
  end
end