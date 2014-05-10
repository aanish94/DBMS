package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

	private File        File;
	private TupleDesc   Td;
	private RandomAccessFile readpagefile;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f
	 *            the file that stores the on-disk backing store for this heap
	 *            file.
	 */
	public HeapFile(File f, TupleDesc td) {
		File   = f;
		try {
			readpagefile = new RandomAccessFile(f, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Td = td;
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		return File;
	}

	/**
	 * Returns an ID uniquely identifying this HeapFile. Implementation note:
	 * you will need to generate this tableid somewhere ensure that each
	 * HeapFile has a "unique id," and that you always return the same value for
	 * a particular HeapFile. We suggest hashing the absolute file name of the
	 * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
	 * 
	 * @return an ID uniquely identifying this HeapFile.
	 */
	public int getId() {
		return File.getAbsolutePath().hashCode();
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		return Td;
	}

	// see DbFile.java for javadocs
	public Page readPage(PageId pid) 
	{
		try {
			byte[] buffer = new byte[BufferPool.PAGE_SIZE];
			long offset = pid.pageNumber()*BufferPool.PAGE_SIZE;
			readpagefile.seek(offset);
			readpagefile.read(buffer);
			HeapPage page = null;
			page = new HeapPage((HeapPageId) pid, buffer);
			return page;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	// see DbFile.java for javadocs
	public void writePage(Page page) throws IOException 
	{
        RandomAccessFile f = new RandomAccessFile(File, "rw"); // rws forces a flush of both data and metadata
        f.seek(page.getId().pageNumber() * BufferPool.PAGE_SIZE);
        f.write(page.getPageData());
        f.close();
	}

	/**
	 * Returns the number of pages in this HeapFile.
	 */
	public int numPages() {
		return (int)(File.length() / BufferPool.PAGE_SIZE);
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
			throws DbException, IOException, TransactionAbortedException 
	{

        List<Page> pagesarr = new ArrayList<Page>();
      
        for(int i = 0; i < numPages(); i++)
        {
          PageId pid = new HeapPageId(this.getId(), i);
          HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
          if(p.getNumEmptySlots() > 0)
          {
             p.insertTuple(t);
             pagesarr.add(p);
             return (ArrayList<Page>)pagesarr;
          }      
        }
        
        HeapPageId insertpage = new HeapPageId(getId(), numPages());
        HeapPage newPage = (HeapPage)Database.getBufferPool().getPage(tid, insertpage, Permissions.READ_WRITE);
        newPage.insertTuple(t);
        
        RandomAccessFile f = new RandomAccessFile(File, "rw");
        f.seek(numPages() * BufferPool.PAGE_SIZE);
        f.write(newPage.getPageData(), 0, BufferPool.PAGE_SIZE);
        f.close();
        pagesarr.add(newPage);
        return (ArrayList<Page>)pagesarr;
		
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
	TransactionAbortedException 
	{
		List<Page> pagesarr = new ArrayList<Page>();
	      HeapPage p = (HeapPage)Database.getBufferPool().getPage(tid, t.getRecordId().getPageId(), Permissions.READ_WRITE);
	      p.deleteTuple(t);
	      pagesarr.add(p);
	      return (ArrayList<Page>)pagesarr;
	}

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {
		return new HeapFileIterator(this, tid);
	}

	class HeapFileIterator implements DbFileIterator
	{
		TransactionId m_tid;
		HeapFile m_file;
		int m_curP;
		int m_numP;
		HeapPage m_curPage;
		Iterator<Tuple> m_iterator;

		public HeapFileIterator(HeapFile file, TransactionId tid)
		{
			m_file = file;
			m_tid = tid;
			m_numP = m_file.numPages();
			m_curP = 0;
		}
		public void open() throws DbException, TransactionAbortedException {
			BufferPool m_bp = Database.getBufferPool();
			HeapPageId m_hpid = new HeapPageId(m_file.getId(),m_curP);
			m_curPage = (HeapPage) m_bp.getPage(this.m_tid, m_hpid, null);
			m_iterator = m_curPage.iterator();
		}

		public boolean hasNext() throws DbException,
		TransactionAbortedException {
			try 
			{
				if (m_iterator.hasNext()) return true;
				if (++m_curP >= m_numP) return false;
				open();
				return m_iterator.hasNext();
			}
			catch (Exception e) 
			{
				return false;
			}
		}

		public Tuple next() throws DbException, TransactionAbortedException,
		NoSuchElementException {
			if (this.hasNext()) return m_iterator.next();
			throw new NoSuchElementException();
		}

		public void rewind() throws DbException, TransactionAbortedException {
			m_curP = 0;
			this.open();
		}

		public void close() {
			m_curPage = null;
			m_iterator = null;	
		}

	}
}