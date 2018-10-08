package andy.andisolr;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class SolrJTest {
    SolrClient solrClient;

    public SolrJTest(String solrURL) {
        this.solrClient = new HttpSolrClient.Builder(solrURL).build();
    }

    public void close() throws IOException {
        solrClient.close();
    }


    /**
     * 随机增加文档
     *
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<Integer> addDocuments() throws IOException, SolrServerException {
        Collection<SolrInputDocument> documents = new ArrayList();
        List<Integer> ids = new ArrayList<>();

        for (int count = 0; count < 999; count++) {
            SolrInputDocument document = new SolrInputDocument();
            int id = count;

            ids.add(id);

            document.addField("id", id);
            document.addField("name", "user" + count);
            document.addField("price", "100");
            document.addField("description", "新增文档" + count);

            documents.add(document);
        }

        UpdateResponse addResponse = solrClient.add(documents);
        log.info("Add doc size" + documents.size() + " result:" + addResponse.getStatus() + " Qtime:" + addResponse.getQTime());

        UpdateResponse commitResponse = solrClient.commit();
        log.info("commit doc to index" + " result:" + addResponse.getStatus() + " Qtime:" + commitResponse.getQTime());

        return ids;
    }


    public List<Object> queryDocuments() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery();

        log.info("======================query===================");

        solrQuery.set("q", "*:*");
        solrQuery.set("start", 0);
        solrQuery.set("rows", 20);
        solrQuery.set("sort", "id asc");


        List<Object> ids = new ArrayList<>();
        QueryResponse queryResponse = solrClient.query(solrQuery);
        SolrDocumentList documents = queryResponse.getResults();
        log.info("查询内容:" + solrQuery);
        log.info("文档数量：" + documents.getNumFound());
        log.info("查询花费时间:" + queryResponse.getQTime());

        log.info("------query data:------");
        for (SolrDocument doc : documents) {
            log.info("document:{}", doc);
            Object objId = doc.getFieldValue("id");
            ids.add(objId);
        }
        log.info("-----------------------");

        return ids;
    }


    public void deleteDocumentById(String id) throws SolrServerException, IOException {
        log.info("将要删除以下文档: id = {}", id);


        UpdateResponse response = solrClient.deleteById(id);
        solrClient.commit();
        log.info("delete id:" + id + " result:" + response.getStatus() + " Qtime:" + response.getQTime());
    }

    public void deleteDocumentByQuery(String query) throws IOException, SolrServerException {
        log.info("======================deleteDocumentByQuery ===================");

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.deleteByQuery(query);
        updateRequest.setCommitWithin(500);
        UpdateResponse response = updateRequest.process(solrClient);
        log.info("url:" + updateRequest.getPath() + "\t xml:" + updateRequest.getXML() + " method:" + updateRequest.getMethod());
        log.info("response:" + response);
    }

    public void updateDocuments(int id, String fieldName, Object fieldValue) throws IOException, SolrServerException {
        log.info("======================updateField ===================");
        HashMap<String, Object> operations = new HashMap();

        operations.put("set", fieldValue);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        doc.addField(fieldName, operations);

        UpdateResponse addResponse = solrClient.add(doc);
        log.info("update doc id:" + id + " result:" + addResponse.getStatus() + " Qtime:" + addResponse.getQTime());
        UpdateResponse commitResponse = solrClient.commit();
        log.info("commit doc to index" + " result:" + commitResponse.getStatus() + " Qtime:" + commitResponse.getQTime());
    }

    public static void main(String args[]) throws IOException, SolrServerException {
        // 假设 Solr 服务器在本地运行，使用端口 8984, 并且有一个 Solr Core 名称为 firstCore
        SolrJTest tester = new SolrJTest("http://localhost:8984/solr/firstCore");

        //添加文档
        List<Integer> addedIds = tester.addDocuments();

        // 删除文档
        tester.deleteDocumentByQuery("name:user3");

        //更新文档
        for (Integer id : addedIds) {
            tester.updateDocuments(id, "name", "更新用户名" + id);
        }


        // 查询文档
        List<Object> allDocumentIds = tester.queryDocuments();

        // 删除本方法添加的文档
        for (Integer id : addedIds) {
            tester.deleteDocumentById("" + id);
        }


        // 删除所有文档
        for (Object id : allDocumentIds) {
            tester.deleteDocumentById("" + id);
        }

        tester.close();
    }
}
