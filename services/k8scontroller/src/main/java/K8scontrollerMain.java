/**
 * Created by jkong on 6/23/18.
 */
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;

public class K8scontrollerMain {
    public static void main(String[] args) throws IOException, ApiException{
        K8scontrollerMain controller = new K8scontrollerMain();
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        controller.ListAllPods(api);


    }
    private void ListAllPods(CoreV1Api api) throws IOException, ApiException{
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }
}
