package club.beifoo.sparrow.edge;

public interface RequestFilter {
	//TODO 实现类要定制化toString方法
	void filter(FilterContext ctx) throws Exception;
	void endDownload(FilterContext ctx) throws Exception;
	void endUpload(FilterContext ctx) throws Exception;
}
