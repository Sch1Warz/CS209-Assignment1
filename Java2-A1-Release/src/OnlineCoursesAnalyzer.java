import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is just a demo for you, please run it on JDK17.
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {
    private final List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                String[] arr = info[2].split("/");
                String time = arr[2] + "-" + arr[0] + "-" + arr[1];
                Date date = ft.parse(time);
                Course course = new Course(info[0], info[1], date, info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]),
                        Integer.parseInt(info[8]), Integer.parseInt(info[9]),
                        Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                        Double.parseDouble(info[14]), Double.parseDouble(info[15]),
                        Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                        Double.parseDouble(info[20]), Double.parseDouble(info[21]),
                        Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> a = new TreeMap<>();
        Integer count;
        for (Course c : courses) {
            if ((count = a.get(c.getInstitution())) == null) {
                a.put(c.getInstitution(), c.participants);
            } else {
                a.put(c.getInstitution(), c.participants + count);
            }
        }
        Set<String> set = a.keySet();
        Object[] arr = set.toArray();
        Arrays.sort((arr));
        Map<String, Integer> b = new TreeMap<>();
        for (Object key : arr) {
            b.put(key.toString(), a.get(key.toString()));
        }
        return b;
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> a = new TreeMap<>();
        Integer count;
        for (Course c : courses) {
            if ((count = a.get(c.institution + "-" + c.subject)) == null) {
                a.put(c.institution + "-" + c.subject, c.participants);
            } else {
                a.put(c.institution + "-" + c.subject, c.participants + count);
            }
        }

        Set<String> set = a.keySet();
        Object[] arr = set.toArray();
        Arrays.sort((arr));
        Map<String, Integer> b = new TreeMap<>();
        for (Object key : arr) {
            b.put(key.toString(), a.get(key.toString()));
        }

        Map<String, Integer> sortedMapDesc = b.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        return sortedMapDesc;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> a = new HashMap<>();
        List<List<String>> count;
        for (Course c : courses) {
            String[] arr = c.instructors.split(", ");
            int which;
            if (arr.length > 1) {
                which = 1;
            } else {
                which = 0;
            }
            for (String i : arr) {
                if ((count = a.get(i)) == null) {
                    List<List<String>> temp = new ArrayList<>();
                    temp.add(new ArrayList<>());
                    temp.add(new ArrayList<>());
                    temp.get(which).add(c.title);
                    a.put(i, temp);
                } else {
                    if (!count.get(which).contains(c.title)) {
                        count.get(which).add(c.title);
                        a.put(i, count);
                    }

                }
            }
        }
        for (String key : a.keySet()) {
            List<List<String>> ans = a.get(key);
            Collections.sort(ans.get(0));
            Collections.sort(ans.get(1));
            a.put(key, ans);
        }
        return a;
    }

    //4
    public List<String> getCourses(int topK, String by) {


        if (Objects.equals(by, "hours")) {
            Map<String, Double> a = new TreeMap<>();
            Double count;
            for (Course c : courses) {
                if ((count = a.get(c.title)) == null) {
                    a.put(c.title, c.totalHours);
                } else {
                    if (c.totalHours > count) {
                        a.put(c.title, c.totalHours);
                    }
                }
            }
            Set<String> set = a.keySet();
            Object[] arr = set.toArray();
            Arrays.sort((arr));
            Map<String, Double> temp = new TreeMap<>();
            for (Object key : arr) {
                temp.put(key.toString(), a.get(key.toString()));
            }
            Map<String, Double> sortedMapDesc = temp.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));
            List<String> ans = new ArrayList<>();
            int stop = 0;
            for (String key : sortedMapDesc.keySet()) {
                ans.add(key);
                stop++;
                if (stop == topK) {
                    break;
                }
            }
            return ans;
        } else {
            Map<String, Integer> a = new TreeMap<>();
            Integer count;
            for (Course c : courses) {
                if ((count = a.get(c.title)) == null) {
                    a.put(c.title, c.participants);
                } else {
                    if (c.participants > count) {
                        a.put(c.title, c.participants);
                    }
                }
            }
            Set<String> set = a.keySet();
            Object[] arr = set.toArray();
            Arrays.sort((arr));
            Map<String, Integer> temp = new TreeMap<>();
            for (Object key : arr) {
                temp.put(key.toString(), a.get(key.toString()));
            }

            Map<String, Integer> sortedMapDesc = temp.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));

            List<String> ans = new ArrayList<>();
            int stop = 0;
            for (String key : sortedMapDesc.keySet()) {
                ans.add(key);
                stop++;
                if (stop == topK) {
                    break;
                }
            }
            return ans;
        }
    }

    //5
    public List<String> searchCourses(String courseSubject, double perAudited, double totalHours) {
        Pattern pattern = Pattern.compile(courseSubject, Pattern.CASE_INSENSITIVE);
        List<String> a = new ArrayList<>();
        for (Course c : courses) {
            Matcher matcher = pattern.matcher(c.subject);
            if (matcher.find()
                    && c.percentAudited >= perAudited
                    && c.totalHours <= totalHours
                    && !a.contains(c.title)) {
                a.add(c.title);
            }
        }
        Collections.sort(a);

        return a;
    }

    //6
    public List<String> recommendCourses(int age,
                                         int gender,
                                         int isBachelorOrHigher) throws ParseException {
        Map<String, String> a = new TreeMap<>();
        String count;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String time = ft.format(date);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        for (Course c : courses) {
            if ((count = a.get(c.number)) == null) {
                a.put(c.number, c.title + "/" + sdf1.format(c.launchDate));
            } else {
                Date nowdate = ft.parse(count.split("/")[1]);
                if (c.launchDate.compareTo(nowdate) > 0) {
                    a.put(c.number, c.title + "/" + sdf1.format(c.launchDate));
                }

            }
        }
        Map<String, List<String>> a1 = new TreeMap<>();
        List<String> s;
        for (Course c : courses) {
            if ((s = a1.get(c.number)) == null) {
                List<String> temp = new ArrayList<>();
                temp.add(c.medianAge + "");
                temp.add(c.percentMale + "");
                temp.add(c.percentDegree + "");
                a1.put(c.number, temp);
            } else {
                String s0 = s.get(0) + "/" + c.medianAge;
                String s1 = s.get(1) + "/" + c.percentMale;
                String s2 = s.get(2) + "/" + c.percentDegree;
                s.set(0, s0);
                s.set(1, s1);
                s.set(2, s2);
                a1.put(c.number, s);
            }
        }
        Map<String, Double> averageAge = new HashMap<>();
        Map<String, Double> percentMale = new HashMap<>();
        Map<String, Double> piDegree = new HashMap<>();
        for (String key : a1.keySet()) {
            String[] averageage = a1.get(key).get(0).split("/");
            String[] pmale = a1.get(key).get(1).split("/");
            String[] pdegree = a1.get(key).get(2).split("/");
            double temp = 0.0;
            for (String ss : averageage) {
                temp += Double.parseDouble(ss);
            }
            temp = temp / averageage.length;
            averageAge.put(key, temp);

            temp = 0.0;
            for (String ss : pmale) {
                temp += Double.parseDouble(ss);
            }
            temp = temp / pmale.length;
            percentMale.put(key, temp);

            temp = 0.0;
            for (String ss : pdegree) {
                temp += Double.parseDouble(ss);
            }
            temp = temp / pdegree.length;
            piDegree.put(key, temp);
        }


        Map<String, Double> b = new TreeMap<>();
        Double count1;
        Map<String, String> a2 = new TreeMap<>();
        for (String ss : a.keySet()) {
            a2.put(ss, a.get(ss).split("/")[0]);
        }
        Map<String, String> a3 = a2.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        for (String ss : a3.keySet()) {
            String title = a3.get(ss);
            if ((count1 = b.get(title)) == null) {
                count1 = Math.pow(age - averageAge.get(ss), 2)
                        + Math.pow(gender * 100 - percentMale.get(ss), 2)
                        + Math.pow(isBachelorOrHigher * 100 - piDegree.get(ss), 2);
                b.put(title, count1);
            } else {
                Double temp = Math.pow(age - averageAge.get(ss), 2)
                        + Math.pow(gender * 100 - percentMale.get(ss), 2)
                        + Math.pow(isBachelorOrHigher * 100 - piDegree.get(ss), 2);
                if (temp < count1) {
                    b.put(title, temp);
                }
            }

        }

        Map<String, Double> sortedMapDesc = b.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        int countt = 0;
        List<String> ans = new ArrayList<>();
        for (String key : sortedMapDesc.keySet()) {
            countt++;
            ans.add(key);
            if (countt == 10) {
                break;
            }
        }
        return ans;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }
}