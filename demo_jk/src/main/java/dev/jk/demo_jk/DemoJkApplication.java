package dev.jk.demo_jk;

import java.io.FileReader;
import java.io.IOException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

@SpringBootApplication
public class DemoJkApplication {
	public static void main(String[] args) throws IOException, ApiException {
		String kubeConfigPath = "jk";
		ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
		// set the global default api-client to the in-cluster one from above
		Configuration.setDefaultApiClient(client);
		// the CoreV1Api loads default api-client from global configuration.
		CoreV1Api api = new CoreV1Api();
		// invokes the CoreV1Api client
		V1ServiceList list = api.listNamespacedService("entando", "", null, null, null, "EntandoPlugin", null, null,
				null, null, null);
		// list.getItems().forEach(e->{System.out.println(e.getMetadata().getLabels().get("entando.org/deployment"));}
		// );
		list.getItems().forEach(e -> {
			System.out.println(e.getSpec().getClusterIP());
		});
		// list.getItems().forEach(e->{System.out.println(e.getMetadata().getGenerateName());}
		// );
		list.getItems().forEach(e -> {
			try {

				V1PodList ll = api.listNamespacedPod("entando", "pretty", null, null, null,
						"EntandoPlugin=" + e.getMetadata().getLabels().get("entando.org/deployment").toString(), null,
						null, null, null, null);
				// ll.getItems().forEach(f->{System.out.println(f.getMetadata().getName());});
				// ll.getItems().forEach(l->{l.getSpec().getContainers().forEach(cd->{cd.getEnv().forEach(ge->{
				// System.out.println( "---->"+ ge); }); });;} );
				ll.getItems().forEach(l -> {
					l.getSpec().getContainers().forEach(cd -> {
						cd.getEnv().forEach(ge -> {

							if (ge.getName().equals("SERVER_SERVLET_CONTEXT_PATH")) {
								System.out.println(ge.getValue());
							} else {
								// System.out.println("not present");
								/// System.out.println("----------> "+ge.getName());

							}

						});
					});
					;
				});
			} catch (ApiException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			;
		});
		// V1ServiceList s = api.listNamespacedService("entando", "true", null, null,
		// null, null, null, null, null, null, null);
		// s.getItems().forEach(e -> {System.out.println(e.getSpec().getClusterIP());});
		// System.out.println(s);
		// List<String> ls = new ArrayList<String>();
		// list.getItems().forEach(I-> ls.add(I.getStatus().getPodIP()));
		// System.out.println(list);
	}
}