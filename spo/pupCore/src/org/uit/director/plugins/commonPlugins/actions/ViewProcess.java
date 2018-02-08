/*
 * Created on 01.10.2007
 * 
 */
package org.uit.director.plugins.commonPlugins.actions;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParserFactory;

import main.ACM;

import org.hsqldb.lib.StringInputStream;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.plugins.PluginInterface;
import org.uit.director.tasks.ProcessInfo;

import ru.masterdm.spo.enums.CrimsonXmlDecoder;
import ru.md.spo.util.Config;

/*
 * author svaliev@masterdm.ru
 */
public class ViewProcess implements PluginInterface {

	HttpServletResponse response;

	WorkflowSessionContext wsc;

	Long idProcess;

	public void init(WorkflowSessionContext wsc, List params) {

		HttpServletRequest request = (HttpServletRequest) params.get(2);
		String idPrStr = request.getParameter("idProcess");
		if (idPrStr != null) {
			idProcess = Long.valueOf(idPrStr);
		}
		response = (HttpServletResponse) params.get(3);
		this.wsc = wsc;
	}

	public String execute() {
		try {

			ProcessInfo procInfo = new ProcessInfo();

			String adminLogin = Config.getProperty("ADMINISTRATOR")
					.toLowerCase();

			procInfo.init(wsc, idProcess, WPC.getInstance().getUsersMgr()
					.getActiveIdUserByLogin(adminLogin), true);

			procInfo.execute();

			Integer idType = procInfo.getIdTypeProcess();

			String schemaImage = (String) WPC.getInstance().getSchemaImageMap()
					.get(idType);
			JGraph graph = new JGraph();

			/*System.out.println("!!!!!!!!!!!!!!!!start");
			System.out.println(schemaImage);
			System.out.println("!!!!!!!!!!!!!!!!finish");
			System.out.println(SAXParserFactory.newInstance().getClass().getName());*/
			
			/* orig code
			XMLDecoder dec = new XMLDecoder(new BufferedInputStream(
					new StringInputStream(schemaImage)));*/
			
			CrimsonXmlDecoder dec = new CrimsonXmlDecoder(new BufferedInputStream(
					new StringInputStream(schemaImage)));

			graph = (JGraph) dec.readObject();

			List<Long> activeStages = procInfo.getActiveStages();

			if (activeStages != null) {

				List<String> nameStages = new ArrayList<String>();

				for (Long idSt : activeStages) {
					String nameStage = (String) WPC.getInstance().getData(
							Cnst.TBLS.stages, idSt, Cnst.TStages.name);
					nameStages.add(nameStage);

				}

				/*
				 * XMLDecoder dec = new XMLDecoder(new BufferedInputStream( new
				 * FileInputStream("d:\\1.xml")));
				 */

				// graph.setPortsVisible(false);
				GraphModel model = graph.getModel();

				for (int i = 0; i < model.getRootCount(); i++) {
					Object cell = model.getRootAt(i);
					if (! (cell instanceof DefaultEdge )) {
						DefaultGraphCell stage = (DefaultGraphCell) cell;
						String name = ACM.nameViewToModel(stage.toString());
						if (name != null && nameStages.contains(name)) {

							Map<DefaultGraphCell, Map<?, ?>> nested = new Hashtable<DefaultGraphCell, Map<?, ?>>();
							Map attributeMap1 = new Hashtable();
							GraphConstants.setGradientColor(attributeMap1,
									Color.RED);
							nested.put(stage, attributeMap1);
							graph.getGraphLayoutCache().edit(nested, null,
									null, null);
						}

					}
				}
			}

			OutputStream out = response.getOutputStream(); // Replace with your
			// output stream
			Color bg = Color.WHITE; // Use this to make the background
			// transparent
			// bg = graph.getBackground(); // Use this to use the graph
			// background color
			BufferedImage img = graph.getImage(bg, 0);

			ImageIO.write(img, "png", out);
			/*
			 * out.flush(); out.close();
			 */

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "errorPage";
		} catch (IOException e) {
			e.printStackTrace();
			return "errorPage";
		}

		return null;
	}

	/*public static void main(String[] args) {
		
		JGraph graph = new JGraph();

		XMLDecoder dec = null;
		try {
			dec = new XMLDecoder(new BufferedInputStream( new FileInputStream("d:\\1.xml")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		

		graph = (JGraph) dec.readObject();
		GraphModel model = graph.getModel();
		
		for (int i = 0; i < model.getRootCount(); i++) {
			Object cell = model.getRootAt(i);
			if (! (cell instanceof DefaultEdge )) {
				DefaultGraphCell stage = (DefaultGraphCell) cell;
				String ss = stage.toString();				
				String name = ACM.nameViewToModel(ss);
				System.out.println("Graph=" + ss + "   Model=" + name);

			}
		}
		

	}*/
}