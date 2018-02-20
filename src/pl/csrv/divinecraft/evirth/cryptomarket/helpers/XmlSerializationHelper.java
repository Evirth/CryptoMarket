package pl.csrv.divinecraft.evirth.cryptomarket.helpers;

import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Paths;

public final class XmlSerializationHelper {

    public static void XmlSerialize(String fileName, Object object) throws JAXBException {
        File file = new File(Paths.get(CryptoMarket.pluginDir, "Players", String.format("%s.xml", fileName)).toString());
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(object, file);
    }

    public static Object XmlDeserialize(String fileName) throws JAXBException {
        File file = new File(Paths.get(CryptoMarket.pluginDir, "Players", String.format("%s.xml", fileName)).toString());
        JAXBContext jaxbContext = JAXBContext.newInstance(PlayerAccount.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return jaxbUnmarshaller.unmarshal(file);
    }
}
