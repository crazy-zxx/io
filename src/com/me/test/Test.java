package com.me.test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Test {

    public static void main(String[] args) {

        //在Java中，InputStream代表输入字节流，OuputStream代表输出字节流，这是最基本的两种IO流。
        //Java提供了Reader和Writer表示字符流，字符流传输的最小数据单位是char。
        //Reader和Writer本质上是一个能自动编解码的InputStream和OutputStream。
        //如果数据源不是文本，就只能使用InputStream，如果数据源是文本，使用Reader更方便一些
        //Java标准库的包java.io提供了同步IO，而java.nio则是异步IO

        //Java的标准库java.io提供了File对象来操作文件和目录。
        //构造File对象时，既可以传入绝对路径，也可以传入相对路径。绝对路径是以根目录开头的完整路径:
        //注意Windows平台使用\作为路径分隔符，在Java字符串中需要用\\表示一个\。Linux平台使用/作为路径分隔符
        File f1 = new File("C:\\Windows\\notepad.exe");
        System.out.println(f1);
        //传入相对路径时，相对路径前面加上当前目录就是绝对路径：
        //可以用.表示当前目录，..表示上级目录
        // 假设当前目录是C:\Docs
        File f11 = new File("sub\\javac"); // 绝对路径是C:\Docs\sub\javac
        File f22 = new File(".\\sub\\javac"); // 绝对路径是C:\Docs\sub\javac
        File f33 = new File("..\\sub\\javac"); // 绝对路径是C:\sub\javac

        //File对象有3种形式表示的路径:
        // 一种是getPath()，返回构造方法传入的路径，
        // 一种是getAbsolutePath()，返回绝对路径，
        // 一种是getCanonicalPath，它和绝对路径类似，但是返回的是规范路径。
        File f2 = new File("..");
        System.out.println(f2.getPath());
        System.out.println(f2.getAbsolutePath());
        try {
            System.out.println(f2.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //因为Windows和Linux的路径分隔符不同，File对象有一个静态变量用于表示当前平台的系统分隔符：
        System.out.println(File.separator); // 根据当前平台打印"\"或"/"

        //构造一个File对象，即使传入的文件或目录不存在，代码也不会出错，因为构造一个File对象，并不会导致任何磁盘操作
        File f111 = new File("C:\\Windows");
        File f222 = new File("C:\\Windows\\notepad.exe");
        File f333 = new File("C:\\Windows\\nothing");
        System.out.println(f111.isFile());
        System.out.println(f111.isDirectory());
        System.out.println(f222.isFile());
        System.out.println(f222.isDirectory());
        System.out.println(f333.isFile());
        System.out.println(f333.isDirectory());

        //当File对象表示一个文件时，可以通过createNewFile()创建一个新文件，用delete()删除该文件
        //File对象提供了createTempFile()来创建一个临时文件，以及deleteOnExit()在JVM退出时自动删除该文件

        //当File对象表示一个目录时，可以使用list()和listFiles()列出目录下的文件和子目录名。listFiles()提供了一系列重载方法，可以过滤不想要的文件和目录
        File f = new File("C:\\Windows");
        File[] fs1 = f.listFiles(); // 列出所有文件和子目录
        printFiles(fs1);
        //文件类型过滤
        File[] fs2 = f.listFiles(new FilenameFilter() {
            // 仅列出.exe文件
            public boolean accept(File dir, String name) {
                return name.endsWith(".exe"); // 返回true表示接受该文件
            }
        });
        printFiles(fs2);

        //Java标准库还提供了一个Path对象，它位于java.nio.file包。Path对象和File对象类似，但操作更加简单：
        //如果需要对目录进行复杂的拼接、遍历等操作，使用Path对象更方便
        Path p1 = Paths.get(".", "project", "study"); // 构造一个Path对象
        System.out.println(p1);
        Path p2 = p1.toAbsolutePath(); // 转换为绝对路径
        System.out.println(p2);
        Path p3 = p2.normalize(); // 转换为规范路径
        System.out.println(p3);
        File ff = p3.toFile(); // 转换为File对象
        System.out.println(ff);
        for (Path p : Paths.get("..").toAbsolutePath()) { // 可以直接遍历Path
            System.out.println("  " + p);
        }

        File fd = new File(".");
        System.out.println(fd.getPath());
        System.out.println(fd.getAbsolutePath());
        try {
            System.out.println(fd.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //列出指定目录下的所有子目录和文件，并按层次打印。
        System.out.println("---------------------");
        listFiles();
        System.out.println("---------------------");
        listFiles("D:\\code\\java\\Test");
        System.out.println("---------------------");


        //InputStream并不是一个接口，而是一个抽象类，它是所有输入流的超类。
        // 这个抽象类定义的一个最重要的方法就是int read()
        // read()会读取输入流的下一个字节，并返回字节表示的int值（0~255）。如果已读到末尾，返回-1表示不能继续读取了。
        // 创建一个FileInputStream对象，完整地读取一个FileInputStream的所有字节：
        /*
        InputStream input = null;
        try {
            //有可能出异常
            input = new FileInputStream("io.iml");
            int n;
            //有可能出异常
            while ((n = input.read()) != -1) { // 利用while同时读取并判断
                System.out.println(n);
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (input != null) {
                try {
                    //也有可能出异常
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
         */

        //所有与IO操作相关的代码都必须正确处理IOException。
        //更好的写法是利用Java 7引入的新的  try(resource)  的语法，
        // 只需要编写try语句，让编译器自动为我们关闭资源。推荐的写法如下：
        try (InputStream input2 = new FileInputStream("io.iml")) {
            int n;
            while ((n = input2.read()) != -1) {
                System.out.println(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }// 编译器在此自动为我们写入finally并调用close()
        //编译器只看try(resource = ...)中的对象是否实现了java.lang.AutoCloseable接口，
        // 如果实现了，就自动加上finally语句并调用close()方法。

        //InputStream提供了两个重载方法来支持读取多个字节：
        //int read(byte[] b)：读取若干字节并填充到byte[]数组，返回读取的字节数
        //int read(byte[] b, int off, int len)：指定byte[]数组的偏移量和最大填充数
        //read()方法的返回值不再是字节的int值，而是返回实际读取了多少个字节。如果返回-1，表示没有更多的数据了。
        try (InputStream input = new FileInputStream("io.iml")) {
            // 定义1000个字节大小的缓冲区:
            byte[] buffer = new byte[1000];
            int n;
            //read()方法是阻塞（Blocking）的,必须等待read()方法返回才能执行下一行代码
            while ((n = input.read(buffer)) != -1) { // 读取到缓冲区
                System.out.println("read " + n + " bytes.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ByteArrayInputStream可以在内存中模拟一个InputStream
        byte[] data = {72, 101, 108, 108, 111, 33};
        StringBuilder sb = new StringBuilder();
        try (InputStream input = new ByteArrayInputStream(data)) {
            int n;
            while ((n = input.read()) != -1) {
                System.out.println((char) n);
                sb.append((char) n);
            }
            System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //OutputStream也是抽象类，它是所有输出流的超类。
        // 这个抽象类定义的一个最重要的方法就是void write(int b)
        //虽然传入的是int参数，但只会写入一个字节，即只写入int最低8位表示字节的部分（相当于b & 0xff）
        //OutputStream也提供了close()方法关闭输出流，以便释放系统资源。
        //要特别注意：OutputStream还提供了一个flush()方法，它的目的是将缓冲区的内容真正输出到目的地。
        //通常情况下，我们不需要调用这个flush()方法，因为缓冲区写满了OutputStream会自动调用它，
        //并且，在调用close()方法关闭OutputStream之前，也会自动调用flush()方法。

        //OutputStream的write()方法也是阻塞的
        //一次性写入若干字节,可以用OutputStream提供的重载方法void write(byte[])来实现：
        try (OutputStream output = new FileOutputStream("out/readme.txt")) {
            output.write("Hello".getBytes("UTF-8")); // Hello
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 编译器在此自动为我们写入finally并调用close()

        //ByteArrayOutputStream可以在内存中模拟一个OutputStream
        byte[] data2;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            output.write("Hello ".getBytes("UTF-8"));
            output.write("world!".getBytes("UTF-8"));
            data2 = output.toByteArray();
            System.out.println(new String(data2, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //利用InputStream和OutputStream，编写一个复制文件的程序
        copyFile("src/source.txt", "srcc/target.txt");

        //通过一个“基础”组件再叠加各种“附加”功能组件的模式，称之为Filter模式（或者装饰器模式：Decorator）
        //JDK首先将InputStream分为两大类：
        //一类是直接提供数据的基础InputStream,一类是提供额外附加功能的InputStream
        //当我们需要给一个“基础”InputStream附加各种功能时，我们先确定这个能提供数据源的InputStream
        //我们希望FileInputStream能提供缓冲的功能来提高读取的效率，因此我们用BufferedInputStream包装这个InputStream，
        // 得到的包装类型是BufferedInputStream，但它仍然被视为一个InputStream
        /*
        InputStream file = new FileInputStream("test.gz");
        InputStream buffered = new BufferedInputStream(file);
        InputStream gzip = new GZIPInputStream(buffered);
         */

        //可以自己编写FilterInputStream，以便可以把自己的FilterInputStream“叠加”到任何一个InputStream中
        byte[] datas = new byte[0];
        try {
            datas = "hello, world !".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //在叠加多个FilterInputStream，我们只需要持有最外层的InputStream，
        // 并且,当最外层的InputStream关闭时（在try(resource)块的结束处自动关闭），
        //内层的InputStream的close()方法也会被自动调用，并最终调用到最核心的“基础”InputStream，因此不存在资源泄露。
        try (CountInputStream in = new CountInputStream(new ByteArrayInputStream(datas))) {
            int n;
            while ((n = in.read()) != -1) {
                System.out.println((char) n);
            }
            System.out.println("Total read " + in.getCount() + " bytes");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ZipInputStream是一种FilterInputStream，它可以直接读取zip包的内容
        //通常是传入一个FileInputStream作为数据源，然后，循环调用getNextEntry()，直到返回null，表示zip流结束。
        /*
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream("..."))) {
            //一个ZipEntry表示一个压缩文件或目录
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                //如果是压缩文件，我们就用read()方法不断读取，直到返回-1：
                if (!entry.isDirectory()) {
                    int n;
                    while ((n = zip.read()) != -1) {
                        System.out.println(n);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        //ZipOutputStream是一种FilterOutputStream，它可以直接写入内容到zip包。
        // 我们要先创建一个ZipOutputStream，通常是包装一个FileOutputStream，
        // 然后，每写入一个文件前，先调用putNextEntry()，然后用write()写入byte[]数据，
        // 写入完毕后调用closeEntry()结束这个文件的打包。
        //如果要实现目录层次结构，new ZipEntry(name)传入的name要用相对路径。
        /*
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream("..."))) {
            File[] files = ...;
            for (File file : files) {
                zip.putNextEntry(new ZipEntry(file.getName()));

                zip.write(...);

                zip.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        //压缩
        /*
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File("test.zip")))) {
            File file = new File("test");
            ziper(file, out, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        //解压
        /*
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(new File("test.zip")))) {
            unziper(in,"test1");
        } catch (Exception e) {
            e.printStackTrace();
        }
         */

        //把资源存储在classpath中可以避免文件路径依赖.
        //从classpath读取文件就可以避免不同环境下文件路径不一致的问题：
        // 如果我们把default.properties文件放到classpath中，就不用关心它的实际存放路径
        //在classpath中的资源文件，路径 总是以 ／ 开头 ，我们先获取当前的Class对象，
        // 然后调用 getResourceAsStream() 就可以直接从classpath读取任意的资源文件
        //getResourceAsStream()需要特别注意的一点是，如果资源文件不存在，它将返回null
        try (InputStream input = Test.class.getResourceAsStream("/default.properties")) {
            if (input != null) {
                // TODO:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果我们把默认的配置放到jar包中，再从外部文件系统读取一个可选的配置文件，
        // 就可以做到既有默认的配置文件，又可以让用户自己修改配置：
        //Properties props = new Properties();
        //props.load(inputStreamFromClassPath("/default.properties"));
        //props.load(inputStreamFromFile("./conf.properties"));


        //序列化是指把一个Java对象变成二进制内容，本质上就是一个byte[]数组
        // 序列化后可以把byte[]保存到文件中，或者把byte[]通过网络传输到远程，
        // 这样，就相当于把Java对象存储到文件或者通过网络传输出去了。
        //反序列化，即把一个二进制内容（也就是byte[]数组）变回Java对象。
        // 有了反序列化，保存到文件中的byte[]数组又可以“变回”Java对象，
        // 或者从网络上读取byte[]并把它“变回”Java对象。

        //一个Java对象要能序列化，必须实现一个特殊的java.io.Serializable接口
        //Serializable接口没有定义任何方法，它是一个空接口。
        // 我们把这样的空接口称为“标记接口”（Marker Interface），
        // 实现了标记接口的类仅仅是给自身贴了个“标记”，并没有增加任何方法。

        //把一个Java对象变为byte[]数组，需要使用ObjectOutputStream。它负责把一个Java对象写入一个字节流：
        //ObjectOutputStream既可以写入基本类型，如int，boolean，也可以写入String（以UTF-8编码），
        // 还可以写入实现了Serializable接口的Object。
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(buffer)) {
            // 写入int:
            output.writeInt(12345);
            // 写入String:
            output.writeUTF("Hello");
            // 写入Object:
            output.writeObject(Double.valueOf(123.456));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(buffer.toByteArray()));

        //除了能读取基本类型和String类型外，调用readObject()可以直接返回一个Object对象。
        // 要把它变成一个特定类型，必须强制转型。
        //反序列化时，由JVM直接构造出Java对象，不调用构造方法，构造方法内部的代码，在反序列化时根本不可能执行。
        ByteArrayInputStream bin = new ByteArrayInputStream(buffer.toByteArray());
        try (ObjectInputStream input = new ObjectInputStream(bin)) {
            int n = input.readInt();
            String s = input.readUTF();
            Double d = (Double) input.readObject();
            System.out.println(n + "," + s + "," + d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Java的序列化机制仅适用于Java，如果需要与其它语言交换数据，必须使用通用的序列化方法，例如JSON。
        //实际上，Java本身提供的基于对象的序列化和反序列化机制既存在安全性问题，也存在兼容性问题。
        //更好的序列化方法是通过JSON这样的通用数据结构来实现，只输出基本类型（包括String）的内容，
        // 而不存储任何与代码相关的信息。


        //java.io.Reader是所有字符输入流的超类，它最主要的方法是：
        //public int read() throws IOException;
        //这个方法读取字符流的下一个字符，并返回字符表示的int，范围是0~65535。如果已读到末尾，返回-1。
        //FileReader是Reader的一个子类，它可以打开文件并获取Reader。
        // 下面的代码演示了如何完整地读取一个FileReader的所有字符

        //FileReader默认的编码与系统相关
        //要避免乱码问题，我们需要在创建FileReader时指定编码：
        try (Reader reader = new FileReader("src/source.txt", StandardCharsets.UTF_8)) {
            int n = 0;
            while ((n = reader.read()) != -1) {
                System.out.println((char) n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //CharArrayReader可以在内存中模拟一个Reader，它的作用实际上是把一个char[]数组变成一个Reader，这和ByteArrayInputStream非常类似：
        //StringReader可以直接把String作为数据源，它和CharArrayReader几乎一样

        //使用InputStreamReader，可以把一个InputStream转换成一个Reader。
        //既然Reader本质上是一个基于InputStream的byte到char的转换器，那么，如果我们已经有一个InputStream，想把它转换为Reader，是完全可行的。
        // InputStreamReader就是这样一个转换器，它可以把任何InputStream转换为Reader
        // 持有InputStream:
        //InputStream input = new FileInputStream("src/readme.txt");
        // 变换为Reader:
        //Reader reader = new InputStreamReader(input, "UTF-8");

        //通过try (resource)更简洁地改写如下：
        try (Reader reader = new InputStreamReader(new FileInputStream("src/source.txt"), "UTF-8")) {
            // TODO:
        } catch (Exception e) {
            e.printStackTrace();
        }

        //FileWriter就是向文件中写入字符流的Writer。它的使用方法和FileReader类似：
        try (Writer writer = new FileWriter("readme.txt", StandardCharsets.UTF_8)) {
            writer.write('H'); // 写入单个字符
            writer.write("Hello".toCharArray()); // 写入char[]
            writer.write("Hello"); // 写入String
        } catch (Exception e) {
            e.printStackTrace();
        }

        //CharArrayWriter可以在内存中创建一个Writer，它的作用实际上是构造一个缓冲区，
        // 可以写入char，最后得到写入的char[]数组，这和ByteArrayOutputStream非常类似：
        try (CharArrayWriter writer = new CharArrayWriter()) {
            writer.write(65);
            writer.write(66);
            writer.write(67);
            char[] dataw = writer.toCharArray(); // { 'A', 'B', 'C' }
            System.out.println(dataw);
        }

        //StringWriter也是一个基于内存的Writer，它和CharArrayWriter类似。
        // 实际上，StringWriter在内部维护了一个StringBuffer，并对外提供了Writer接口

        //OutputStreamWriter就是一个将任意的OutputStream转换为Writer的转换器：
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("readme.txt"), "UTF-8")) {
            // TODO:
        } catch (Exception e) {
            e.printStackTrace();
        }

        //rintStream最终输出的总是byte数据，
        // 而PrintWriter则是扩展了Writer接口，
        // 它的print()/println()方法最终输出的是char数据。

        //PrintStream是一种FilterOutputStream，它在OutputStream的接口上，
        // 额外提供了一些写入各种数据类型的方法
        //PrintStream和OutputStream相比，除了添加了一组print()/println()方法，可以打印各种数据类型，
        // 比较方便外，它还有一个额外的优点，就是不会抛出IOException，这样我们在编写代码的时候，就不必捕获IOException。
        StringWriter buffers = new StringWriter();
        try (PrintWriter pw = new PrintWriter(buffers)) {
            pw.println("Hello");
            pw.println(12345);
            pw.println(true);
        }
        System.out.println(buffers.toString());
    }

    //解压缩文件
    static void unziper(ZipInputStream in, String dir) throws Exception {

        ZipEntry entry = null;
        //遍历zipentry
        while ((entry = in.getNextEntry()) != null) {
            //由entry构造文件
            File ft = new File(dir + "/" + entry.getName());

            //是文件夹，且不存在，则创建
            if (entry.isDirectory() && !ft.exists()) {
                ft.mkdirs();
            } else {    //是文件，则输出文件

                try (OutputStream out = new FileOutputStream(ft)) {
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }

        }
    }

    //压缩文件（夹）
    static void ziper(File f, ZipOutputStream out, String base) throws Exception {

        for (File ft : f.listFiles()) {

            //文件
            if (ft.isFile()) {
                //添加 文件条目，需要使用带相对路径的文件名
                out.putNextEntry(new ZipEntry(base + ft.getName()));

                InputStream in = new FileInputStream(ft);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                //关闭一个文件条目
                out.closeEntry();
            }

            //目录
            if (ft.isDirectory()) {
                //创建 文件夹条目，需要在相对路径里加 /
                out.putNextEntry(new ZipEntry(base + ft.getName() + "/"));
                //递归文件夹
                ziper(ft, out, base + ft.getName() + "/");
                //该文件夹遍历完才能关闭该文件夹条目
                out.closeEntry();
            }

        }
    }

    //利用InputStream和OutputStream，编写一个复制文件的程序
    static void copyFile(String src, String target) {
        File fs = new File(src);
        File fw = new File(target);
        // 是否为文件
        if (fs.isFile()) {
            //为目标文件创建目录
            File parent = fw.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (InputStream in = new FileInputStream(fs); OutputStream out = new FileOutputStream(fw)) {
                byte[] buffer = new byte[1024];
                int len = 0;
                //读写
                while ((len = in.read(buffer)) != -1) {
                    //将从偏移量为 off的指定字节数组中的 len字节写入此文件输出流。
                    out.write(buffer, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Not a file!");
        }
    }

    static void listFiles() {
        File f = new File(".");
        System.out.println(f.getName() + "/");
        list(f, 1);
    }

    static void listFiles(String filename) {
        File f = new File(filename);
        System.out.println(f.getName() + "/");
        list(f, 1);
    }

    static void list(File f, int level) {

        for (File ft : f.listFiles()) {

            StringBuilder pre = new StringBuilder();
            for (int i = 0; i < level; i++) {
                pre.append("  ");
            }

            if (ft.isFile()) {
                System.out.println(pre + ft.getName());
            }

            if (ft.isDirectory()) {
                System.out.println(pre + ft.getName() + "/");
                list(ft, level + 1);
            }
        }
    }

    static void printFiles(File[] files) {
        System.out.println("==========");
        if (files != null) {
            for (File f : files) {
                System.out.println(f);
            }
        }
        System.out.println("==========");
    }

}

//为了避免这种class定义变动导致的不兼容，Java的序列化允许class定义一个特殊的serialVersionUID静态变量，
// 用于标识Java类的序列化“版本”，通常可以由IDE自动生成。如果增加或修改了字段，
// 可以改变serialVersionUID的值，这样就能自动阻止不匹配的class版本：
class Person implements Serializable {
    private static final long serialVersionUID = 2709425275741743919L;
}

class CountInputStream extends FilterInputStream {

    private int count = 0;

    @Override
    public int read() throws IOException {
        int n = in.read();
        if (n != -1) {
            this.count++;
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int n = in.read(b);
        if (n != -1) {
            this.count += n;
        }
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if (n != -1) {
            this.count += n;
        }
        return n;
    }

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    protected CountInputStream(InputStream in) {    //传入需要包装的InputStream
        super(in);
    }

    public int getCount() {
        return count;
    }

}